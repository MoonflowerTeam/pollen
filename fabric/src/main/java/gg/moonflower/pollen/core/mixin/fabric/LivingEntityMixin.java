package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.EventResult;
import gg.moonflower.pollen.api.event.FabricHooks;
import gg.moonflower.pollen.api.event.events.entity.living.LivingEntityEvents;
import gg.moonflower.pollen.api.event.events.entity.living.PotionEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvents;
import gg.moonflower.pollen.api.registry.FluidBehaviorRegistry;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    private DamageSource captureDamageSource;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        if (!TickEvents.LIVING_PRE.invoker().tick((LivingEntity) (Object) this))
            ci.cancel();
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    public void captureArgs(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
        captureDamageSource = damageSource;
    }

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getHealth()F", shift = At.Shift.BEFORE), ordinal = 0, argsOnly = true)
    public float modifyDamageAmount(float value) {
        LivingEntityEvents.Damage.Context context = new FabricHooks.LivingDamageContextImpl(value);
        boolean event = LivingEntityEvents.DAMAGE.invoker().livingDamage((LivingEntity) (Object) this, captureDamageSource, context);
        return event ? context.getDamageAmount() : 0.0F;
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(DamageSource damageSource, CallbackInfo ci) {
        if (!LivingEntityEvents.DEATH.invoker().death((LivingEntity) (Object) this, damageSource))
            ci.cancel();
    }

    @ModifyVariable(method = "heal", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public float modifyHealAmount(float value) {
        LivingEntityEvents.Heal.HealContext context = new FabricHooks.HealContextImpl(value);
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

    @Inject(method = "addEffect", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.BEFORE))
    public void addEffect(MobEffectInstance effectInstance, CallbackInfoReturnable<Boolean> cir) {
        MobEffectInstance effectinstance = (MobEffectInstance)this.activeEffects.get(effectInstance.getEffect());
        PotionEvents.ADD.invoker().add((LivingEntity) (Object) this, effectInstance, effectinstance);
    }

    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    public void canBeAffected(MobEffectInstance effectInstance, CallbackInfoReturnable<Boolean> cir) {
        EventResult result = PotionEvents.APPLICABLE.invoker().applicable((LivingEntity) (Object) this, effectInstance);
        if (result != EventResult.PASS)
            cir.setReturnValue(result == EventResult.ALLOW);
    }

    @Inject(method = "removeEffect", at = @At("HEAD"), cancellable = true)
    public void removeEffect(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if (!PotionEvents.REMOVE.invoker().remove((LivingEntity) (Object) this, effect))
            cir.setReturnValue(false);
    }

    @Inject(method = "removeAllEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V", shift = At.Shift.BEFORE), cancellable = true)
    public void removeAllEffects(CallbackInfoReturnable<Boolean> cir) {
        Iterator<MobEffectInstance> iterator = this.activeEffects.values().iterator();
        if (!PotionEvents.REMOVE.invoker().remove((LivingEntity) (Object) this, (MobEffect) iterator.next().getEffect()))
            cir.cancel();
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
