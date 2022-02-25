package gg.moonflower.pollen.core.mixin.fabric.sodium;

import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuffers;
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
}
