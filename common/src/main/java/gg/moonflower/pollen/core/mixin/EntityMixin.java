package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.block.PollinatedLiquidBlock;
import gg.moonflower.pollen.api.fluid.PollenFluidBehavior;
import gg.moonflower.pollen.api.registry.FluidBehaviorRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Unique
    private final Set<TagKey<Fluid>> wasInFluids = new HashSet<>();

    @Shadow
    public Level level;

    @Shadow
    public abstract BlockPos blockPosition();

    @Shadow
    protected boolean firstTick;

    @Shadow
    protected abstract void doWaterSplashEffect();

    @Shadow
    @Final
    protected RandomSource random;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "updateInWaterStateAndDoFluidPushing", at = @At("TAIL"))
    public void updateCustomFluids(CallbackInfoReturnable<Boolean> cir) {
        FluidBehaviorRegistry.getFluids().forEach(tag -> {
            if (FluidBehaviorRegistry.doFluidPushing(tag, (Entity) (Object) this)) {
                if (this.wasInFluids.add(tag) && !this.firstTick)
                    this.doWaterSplashEffect();
            } else {
                this.wasInFluids.remove(tag);
            }
        });
    }

    @Inject(method = "getBlockSpeedFactor", at = @At("HEAD"), cancellable = true)
    public void getBlockSpeedFactor(CallbackInfoReturnable<Float> cir) {
        Block block = this.level.getBlockState(this.blockPosition()).getBlock();
        if (block instanceof PollinatedLiquidBlock)
            cir.setReturnValue(block.getSpeedFactor());
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "doWaterSplashEffect", at = @At("HEAD"), cancellable = true)
    public void doCustomFluidSplashEffect(CallbackInfo ci) {
        FluidBehaviorRegistry.getFluids().stream().filter(this.wasInFluids::contains).forEach(tag -> {
            PollenFluidBehavior behavior = Objects.requireNonNull(FluidBehaviorRegistry.get(tag));
            behavior.doSplashEffect((Entity) (Object) this, this.random);
            ci.cancel();
        });
    }
}
