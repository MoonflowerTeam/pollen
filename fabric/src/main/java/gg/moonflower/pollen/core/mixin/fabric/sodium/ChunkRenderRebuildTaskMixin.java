package gg.moonflower.pollen.core.mixin.fabric.sodium;

import gg.moonflower.pollen.core.extensions.fabric.sodium.ChunkRenderDataExtension;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkGraphicsState;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildResult;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderBounds;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderBuildTask;
import me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderRebuildTask;
import me.jellysquid.mods.sodium.client.render.pipeline.context.ChunkRenderCacheLocal;
import me.jellysquid.mods.sodium.client.util.task.CancellationSource;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashSet;
import java.util.Set;

@Mixin(ChunkRenderRebuildTask.class)
public abstract class ChunkRenderRebuildTaskMixin<T extends ChunkGraphicsState> extends ChunkRenderBuildTask<T> {

    @Unique
    private final Set<BlockPos> blockRenderPositions = new HashSet<>();

    @Inject(method = "performBuild", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isSolidRender(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    public void performBuild(ChunkRenderCacheLocal cache, ChunkBuildBuffers buffers, CancellationSource cancellationSource, CallbackInfoReturnable<ChunkBuildResult<T>> cir, ChunkRenderData.Builder renderData, VisGraph occluder, ChunkRenderBounds.Builder bounds, WorldSlice slice, int baseX, int baseY, int baseZ, BlockPos.MutableBlockPos pos, BlockPos renderOffset, int relY, int relZ, int relX, BlockState state) {
        BlockRenderer renderer = BlockRendererRegistry.getFirst(state.getBlock());
        if (renderer != null && renderer.getRenderShape(state) != RenderShape.MODEL)
            this.blockRenderPositions.add(pos.immutable());
    }

    @Inject(method = "performBuild", at = @At("TAIL"), remap = false)
    public void injectRenderPositions(ChunkRenderCacheLocal model, ChunkBuildBuffers seed, CancellationSource layer, CallbackInfoReturnable<ChunkBuildResult<T>> cir) {
        ((ChunkRenderDataExtension) cir.getReturnValue().data).pollen_getBlockRenderPositions().addAll(this.blockRenderPositions);
    }
}
