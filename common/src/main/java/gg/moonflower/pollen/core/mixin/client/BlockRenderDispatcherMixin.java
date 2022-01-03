package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockRenderDispatcher.class)
public class BlockRenderDispatcherMixin {

    @ModifyVariable(method = "getBlockModel", at = @At("HEAD"), argsOnly = true)
    public BlockState modifyBlockState(BlockState state) {
        BlockRenderer renderer = BlockRendererRegistry.getFirst(state.getBlock());
        return renderer != null ? renderer.getRenderState(state) : state;
    }
}
