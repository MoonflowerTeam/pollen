package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.registry.FluidBehaviorRegistry;
import net.minecraft.tags.FluidTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    private LivingEntityMixin(EntityType<?> arg, Level arg2) {
        super(arg, arg2);
    }

    @Shadow
    protected abstract void jumpInLiquid(TagKey<Fluid> fluidTag);

    @ModifyVariable(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidJumpThreshold()D", shift = At.Shift.BEFORE), ordinal = 3)
    public double modifyFluidHeight(double value) {
        return value == 0 ? FluidBehaviorRegistry.getFluids().stream().mapToDouble(this::getFluidHeight).filter(tag -> tag > 0.0).findFirst().orElse(0.0) : value;
    }

    @Inject(method = "jumpInLiquid", at = @At("HEAD"), cancellable = true)
    public void jumpInLiquid(TagKey<Fluid> fluidTag, CallbackInfo ci) {
        if (!this.isInWater() && fluidTag == FluidTags.WATER) {
            FluidBehaviorRegistry.getFluids().stream().filter(tag -> Objects.requireNonNull(FluidBehaviorRegistry.get(tag)).canAscend((LivingEntity) (Object) this) && this.getFluidHeight(tag) > 0.0).findFirst().ifPresent(this::jumpInLiquid);
            ci.cancel();
        }
    }
}
