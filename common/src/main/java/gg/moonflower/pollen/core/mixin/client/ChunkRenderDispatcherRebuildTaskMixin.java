package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.core.extensions.ChunkRenderExtensions;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererDispatcher;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
public class ChunkRenderDispatcherRebuildTaskMixin {

    @Unique
    private RenderChunkRegion captureRegion;

    @Shadow
    @Nullable
    protected RenderChunkRegion region;

    @Inject(method = "compile", at = @At("HEAD"))
    public void captureRegion(float f, float g, float h, ChunkBufferBuilderPack chunkBufferBuilderPack, CallbackInfoReturnable<ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults> cir) {
        this.captureRegion = this.region;
    }

    @Inject(method = "compile", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;<init>()V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    public void compile(float f, float y, float z, ChunkBufferBuilderPack pack, CallbackInfoReturnable<ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults> cir, ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults compileResults, int i, BlockPos originPos) {
        if (this.captureRegion != null) {
            for (BlockPos pos : BlockPos.betweenClosed(originPos, originPos.offset(15, 15, 15))) {
                BlockState state = this.captureRegion.getBlockState(pos);
                if (state.isAir())
                    continue;
                if (BlockRendererDispatcher.shouldRender(state)) {
                    ((ChunkRenderExtensions) (Object) compileResults).pollen_getBlockRenderPositions().add(pos.immutable());
                    if (BlockRendererRegistry.get(state.getBlock()).stream().anyMatch(r -> r instanceof TickableBlockRenderer))
                        ((ChunkRenderExtensions) (Object) compileResults).pollen_getTickingBlockRenderers().add(pos.immutable());
                }
            }
        }
    }
}
