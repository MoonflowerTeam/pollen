package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.events.entity.living.LivingEntityEvents;
import gg.moonflower.pollen.api.event.events.entity.living.PotionEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvents;
import gg.moonflower.pollen.api.registry.FluidBehaviorRegistry;
import gg.moonflower.pollen.common.events.context.HealContextImpl;
import gg.moonflower.pollen.common.events.context.LivingDamageContextImpl;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    private LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    protected abstract void jumpInLiquid(Tag<Fluid> fluidTag);

    @Shadow
    @Final
    private Map<MobEffect, MobEffectInstance> activeEffects;

    @Unique
    private static DamageSource captureDamageSource;

    @Unique
    private static float captureDamageAmount;

    @Unique
    private static float captureHealAmount;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        if (!TickEvents.LIVING_PRE.invoker().tick((LivingEntity) (Object) this))
            ci.cancel();
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    public void captureArgs(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
        captureDamageSource = damageSource;
        captureDamageAmount = damageAmount;
    }

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getHealth()F", shift = At.Shift.BEFORE), ordinal = 0, argsOnly = true)
    public float modifyDamageAmount(float value) {
        LivingEntityEvents.Damage.Context context = new LivingDamageContextImpl(captureDamageAmount);
        boolean event = LivingEntityEvents.DAMAGE.invoker().livingDamage((LivingEntity) (Object) this, captureDamageSource, context);
        return event ? context.getDamageAmount() : 0.0F;
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(DamageSource damageSource, CallbackInfo ci) {
        if (!LivingEntityEvents.DEATH.invoker().death((LivingEntity) (Object) this, damageSource))
            ci.cancel();
    }

    @Inject(method = "heal", at = @At("HEAD"))
    public void heal(float healAmount, CallbackInfo ci) {
        captureHealAmount = healAmount;
    }

    @ModifyVariable(method = "heal", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public float modifyHealAmount(float value) {
        LivingEntityEvents.Heal.HealContext context = new HealContextImpl(captureHealAmount);
        boolean event = LivingEntityEvents.HEAL.invoker().heal((LivingEntity) (Object) this, context);
        return event ? context.getAmount() : 0.0F;
    }

    @Inject(method = "tickEffects", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V", shift = At.Shift.BEFORE))
    public void tickEffects(CallbackInfo ci) {
        Iterator<MobEffect> iterator = this.activeEffects.keySet().iterator();
        MobEffect effect = (MobEffect)iterator.next();
        MobEffectInstance effectinstance = (MobEffectInstance)this.activeEffects.get(effect);
        PotionEvents.EXPIRE.invoker().expire((LivingEntity) (Object) this, effectinstance);
    }

    @ModifyVariable(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidJumpThreshold()D", shift = At.Shift.BEFORE), ordinal = 6)
    public double modifyFluidHeight(double value) {
        return value == 0 ? FluidBehaviorRegistry.getFluids().stream().mapToDouble(this::getFluidHeight).filter(tag -> tag > 0.0).findFirst().orElse(0.0) : value;
    }

    @Inject(method = "jumpInLiquid", at = @At("HEAD"), cancellable = true)
    public void jumpInLiquid(Tag<Fluid> fluidTag, CallbackInfo ci) {
        if (!this.isInWater() && fluidTag == FluidTags.WATER) {
            FluidBehaviorRegistry.getFluids().stream().filter(tag -> Objects.requireNonNull(FluidBehaviorRegistry.get(tag)).canAscend((LivingEntity)(Object)this) && this.getFluidHeight(tag) > 0.0).findFirst().ifPresent(this::jumpInLiquid);
            ci.cancel();
        }
    }
}
