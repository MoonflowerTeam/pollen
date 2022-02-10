package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.event.events.entity.ModifyGravityEvent;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvents;
import gg.moonflower.pollen.api.fluid.PollenFluidBehavior;
import gg.moonflower.pollen.api.registry.FluidBehaviorRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected abstract int decreaseAirSupply(int air);

    @Shadow
    public abstract boolean canBreatheUnderwater();

    @Shadow
    protected abstract void jumpInLiquid(Tag<Fluid> fluidTag);

    @Shadow
    public abstract void calculateEntityAnimation(LivingEntity livingEntity, boolean bl);

    @Shadow
    protected abstract boolean isAffectedByFluids();

    @Shadow
    public abstract boolean canStandOnFluid(Fluid fluid);

    @Unique
    private int captureAirSupply;

    private LivingEntityMixin(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    public void captureState(CallbackInfo ci) {
        this.captureAirSupply = this.getAirSupply();
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInWaterRainOrBubble()Z", shift = At.Shift.BEFORE))
    public void tickFluidDrowning(CallbackInfo ci) {
        if (this.isAlive()) {
            PollenFluidBehavior behavior = this.fluidOnEyes != null ? FluidBehaviorRegistry.get(this.fluidOnEyes) : null;
            if (behavior == null) // No custom behavior
                return;

            LivingEntity livingEntity = (LivingEntity) (Object) this;
            if (!behavior.shouldEntityDrown(livingEntity))
                return;

            if (!this.canBreatheUnderwater() && !MobEffectUtil.hasWaterBreathing(livingEntity) && !((Object) this instanceof Player && ((Player) livingEntity).abilities.invulnerable)) {
                this.setAirSupply(this.captureAirSupply);
                this.setAirSupply(this.decreaseAirSupply(this.getAirSupply()));
                if (this.getAirSupply() == -20) {
                    this.setAirSupply(0);
                    Vec3 vec3 = this.getDeltaMovement();

                    ParticleOptions particle = behavior.getDrowningParticles(livingEntity);
                    if (particle != null) {
                        for (int i = 0; i < 8; ++i) {
                            double f = this.random.nextDouble() - this.random.nextDouble();
                            double g = this.random.nextDouble() - this.random.nextDouble();
                            double h = this.random.nextDouble() - this.random.nextDouble();
                            this.level.addParticle(particle, this.getX() + f, this.getY() + g, this.getZ() + h, vec3.x, vec3.y, vec3.z);
                        }
                    }

                    this.hurt(DamageSource.DROWN, behavior.getDrowningDamage(livingEntity));
                }
            }

            if (!this.level.isClientSide() && this.isPassenger() && this.getVehicle() != null && !behavior.canVehicleTraverse(livingEntity, this.getVehicle()))
                this.stopRiding();
        }
    }

    @Inject(method = "checkFallDamage", at = @At("HEAD"))
    public void updateCustomFluid(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo ci) {
        FluidBehaviorRegistry.getFluids().stream().filter(tag -> this.getFluidHeight(tag) == 0).forEach(tag -> FluidBehaviorRegistry.doFluidPushing(tag, this));
    }

    @ModifyVariable(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidJumpThreshold()D", shift = At.Shift.BEFORE), ordinal = 0)
    public boolean modifyInWater(boolean value) {
        return value || FluidBehaviorRegistry.getFluids().stream().anyMatch(tag -> this.getFluidHeight(tag) > 0.0);
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

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void travelInCustomFluid(Vec3 travelVector, CallbackInfo ci, double fallSpeed, boolean falling) {
        if (this.isAffectedByFluids() && !this.canStandOnFluid(this.level.getFluidState(this.blockPosition()).getType())) {
            FluidBehaviorRegistry.getFluids().stream().filter(tag -> this.getFluidHeight(tag) > 0.0).forEach(tag -> {
                PollenFluidBehavior behavior = Objects.requireNonNull(FluidBehaviorRegistry.get(tag));
                behavior.applyPhysics((LivingEntity) (Object) this, travelVector, fallSpeed, falling);
                ci.cancel();
            });
            if (ci.isCancelled())
                this.calculateEntityAnimation((LivingEntity) (Object) this, this instanceof FlyingAnimal);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        TickEvents.LIVING_POST.invoker().tick((LivingEntity) (Object) this);
    }

    @ModifyVariable(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;", shift = At.Shift.BEFORE))
    public double modifyGravity(double gravity) {
        return ModifyGravityEvent.EVENT.invoker().modifyGravity((LivingEntity) (Object) this, gravity);
    }
}
