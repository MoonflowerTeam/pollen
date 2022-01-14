package gg.moonflower.pollen.core.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.core.extensions.CompiledChunkExtension;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

@Mixin(targets = "net/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask")
public class ChunkRenderDispatcherRebuildTaskMixin {

    @Inject(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isSolidRender(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void compile(float x, float y, float z, ChunkRenderDispatcher.CompiledChunk chunk, ChunkBufferBuilderPack pack, CallbackInfoReturnable<Set<BlockEntity>> cir, int i, BlockPos start, BlockPos end, VisGraph visGraph, Set<BlockEntity> blockEntities, RenderChunkRegion renderChunkRegion, PoseStack matrixStack, Random random, BlockRenderDispatcher blockRenderDispatcher, Iterator<BlockPos> iterator, BlockPos pos, BlockState state) {
        BlockRenderer renderer = BlockRendererRegistry.getFirst(state.getBlock());
        if (renderer != null && renderer.getRenderShape(state) != RenderShape.MODEL)
            ((CompiledChunkExtension) chunk).pollen_getBlockRenderPositions().add(pos.immutable());
    }

    @Redirect(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    public RenderShape redirectRenderShape(BlockState state) {
        BlockRenderer renderer = BlockRendererRegistry.getFirst(state.getBlock());
        return renderer != null && renderer.getRenderShape(state) == RenderShape.INVISIBLE ? RenderShape.INVISIBLE : state.getRenderShape();
    }
}
