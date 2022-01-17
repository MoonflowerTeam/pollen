package gg.moonflower.pollen.core.mixin.fabric.sodium;

import gg.moonflower.pollen.core.extensions.fabric.sodium.ChunkRenderDataExtension;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import me.jellysquid.mods.sodium.client.gl.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildResult;
import me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderBuildTask;
import me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderRebuildTask;
import me.jellysquid.mods.sodium.client.util.task.CancellationSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(ChunkRenderRebuildTask.class)
public abstract class ChunkRenderRebuildTaskMixin extends ChunkRenderBuildTask {

    @Shadow
    @Final
    private RenderSection render;

    @Inject(method = "performBuild", at = @At("TAIL"), remap = false)
    public void injectRenderPositions(ChunkBuildContext context, CancellationSource cancellationSource, CallbackInfoReturnable<ChunkBuildResult> cir) {
        Set<BlockPos> blockRenderPositions = new HashSet<>();
        Set<BlockPos> tickingBlockRenderPositions = new HashSet<>();
        for (BlockPos pos : BlockPos.betweenClosed(this.render.getOriginX(), this.render.getOriginY(), this.render.getOriginZ(), this.render.getOriginX() + 16, this.render.getOriginY() + 16, this.render.getOriginZ() + 16)) {
            BlockState state = context.cache.getWorldSlice().getBlockState(pos);
            if (state.isAir())
                continue;
            BlockRenderer renderer = BlockRendererRegistry.getFirst(state.getBlock());
            if (renderer != null && renderer.getRenderShape(state) != RenderShape.MODEL)
                blockRenderPositions.add(pos.immutable());
            if (BlockRendererRegistry.get(state.getBlock()).stream().anyMatch(r -> r instanceof TickableBlockRenderer))
                tickingBlockRenderPositions.add(pos.immutable());
        }

        ((ChunkRenderDataExtension) cir.getReturnValue().data).pollen_getBlockRenderPositions().addAll(blockRenderPositions);
        ((ChunkRenderDataExtension) cir.getReturnValue().data).pollen_getTickingBlockRenderPositions().addAll(tickingBlockRenderPositions);
    }
}
