package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.block.PollinatedSign;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    public void isValid(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
        if (BlockEntityType.SIGN.equals(this) && blockState.getBlock() instanceof PollinatedSign)
            cir.setReturnValue(true);
    }
}
