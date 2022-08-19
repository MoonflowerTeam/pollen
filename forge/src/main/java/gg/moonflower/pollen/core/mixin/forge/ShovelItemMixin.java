package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.registry.content.FlatteningRegistry;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {

    @Inject(method = "getShovelPathingState", at = @At("HEAD"), cancellable = true, remap = false)
    private static void getShovelPathingState(BlockState state, CallbackInfoReturnable<BlockState> cir) {
        BlockState flattenedState = FlatteningRegistry.getFlattenedState(state);
        if (flattenedState != null)
            cir.setReturnValue(flattenedState);
    }
}
