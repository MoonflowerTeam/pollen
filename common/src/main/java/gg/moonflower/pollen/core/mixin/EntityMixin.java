package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.block.PollinatedLiquidBlock;
import gg.moonflower.pollen.api.registry.FluidBehaviorRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public Level level;

    @Shadow
    public abstract BlockPos blockPosition();

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "updateInWaterStateAndDoFluidPushing", at = @At("TAIL"), cancellable = true)
    public void updateCustomFluids(CallbackInfoReturnable<Boolean> cir) {
        FluidBehaviorRegistry.getFluids().forEach(tag -> FluidBehaviorRegistry.doFluidPushing(tag, (Entity) (Object) this));
    }

    @Inject(method = "getBlockSpeedFactor", at = @At("HEAD"), cancellable = true)
    public void getBlockSpeedFactor(CallbackInfoReturnable<Float> cir) {
        Block block = this.level.getBlockState(this.blockPosition()).getBlock();
        if (block instanceof PollinatedLiquidBlock)
            cir.setReturnValue(block.getSpeedFactor());
    }
}
