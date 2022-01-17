package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.core.extensions.CompiledChunkExtension;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;

@Mixin(targets = "net/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask")
public class ChunkRenderDispatcherRebuildTaskMixin {

    @Shadow
    @Nullable
    protected RenderChunkRegion region;

    @Inject(method = "compile", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/Sets;newHashSet()Ljava/util/HashSet;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    public void compile(float x, float y, float z, ChunkRenderDispatcher.CompiledChunk chunk, ChunkBufferBuilderPack pack, CallbackInfoReturnable<Set<BlockEntity>> cir, int i, BlockPos originPos, BlockPos offset) {
        if (this.region != null) {
            for (BlockPos pos : BlockPos.betweenClosed(originPos, offset)) {
                BlockState state = this.region.getBlockState(pos);
                if (state.isAir())
                    continue;
                BlockRenderer renderer = BlockRendererRegistry.getFirst(state.getBlock());
                if (renderer != null && renderer.getRenderShape(state) != RenderShape.MODEL)
                    ((CompiledChunkExtension) chunk).pollen_getTickingBlockRenderers().add(pos.immutable());
                if (BlockRendererRegistry.get(state.getBlock()).stream().anyMatch(r -> r instanceof TickableBlockRenderer))
                    ((CompiledChunkExtension) chunk).pollen_getTickingBlockRenderers().add(pos.immutable());
            }
        }
    }

    @Redirect(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    public RenderShape redirectRenderShape(BlockState state) {
        BlockRenderer renderer = BlockRendererRegistry.getFirst(state.getBlock());
        return renderer != null && renderer.getRenderShape(state) == RenderShape.INVISIBLE ? RenderShape.INVISIBLE : state.getRenderShape();
    }
}
