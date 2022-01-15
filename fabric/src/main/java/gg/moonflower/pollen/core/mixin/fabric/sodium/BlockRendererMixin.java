package gg.moonflower.pollen.core.mixin.fabric.sodium;

import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer.class)
public class BlockRendererMixin {

    @ModifyVariable(method = "renderModel", at = @At("HEAD"), argsOnly = true, remap = false)
    public BlockState modifyState(BlockState original) {
        BlockRenderer renderer = BlockRendererRegistry.getFirst(original.getBlock());
        return renderer != null ? renderer.getRenderState(original) : original;
    }

    @Inject(method = "renderModel", at = @At("HEAD"), remap = false, cancellable = true)
    public void renderModel(BlockAndTintGetter world, BlockState state, BlockPos pos, BlockPos origin, BakedModel model, ChunkModelBuilder buffers, boolean cull, long seed, CallbackInfoReturnable<Boolean> cir) {
        BlockRenderer renderer = BlockRendererRegistry.getFirst(state.getBlock());
        if (renderer != null && renderer.getRenderShape(state) == RenderShape.INVISIBLE)
            cir.setReturnValue(false);
    }
}
