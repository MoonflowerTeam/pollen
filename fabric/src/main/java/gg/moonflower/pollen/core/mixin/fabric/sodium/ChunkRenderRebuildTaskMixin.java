package gg.moonflower.pollen.core.mixin.fabric.sodium;

import gg.moonflower.pollen.core.extensions.fabric.sodium.ChunkRenderDataExtension;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererDispatcher;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import me.jellysquid.mods.sodium.client.gl.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildResult;
import me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderBuildTask;
import me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderRebuildTask;
import me.jellysquid.mods.sodium.client.util.task.CancellationSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRenderRebuildTask.class)
public abstract class ChunkRenderRebuildTaskMixin extends ChunkRenderBuildTask {

    @Shadow(remap = false)
    @Final
    private RenderSection render;

    @Inject(method = "performBuild", at = @At("TAIL"), remap = false)
    public void injectRenderPositions(ChunkBuildContext context, CancellationSource cancellationSource, CallbackInfoReturnable<ChunkBuildResult> cir) {
        ChunkRenderDataExtension extension = (ChunkRenderDataExtension) cir.getReturnValue().data;

        for (BlockPos pos : BlockPos.betweenClosed(this.render.getOriginX(), this.render.getOriginY(), this.render.getOriginZ(), this.render.getOriginX() + 16, this.render.getOriginY() + 16, this.render.getOriginZ() + 16)) {
            BlockState state = context.cache.getWorldSlice().getBlockState(pos);
            if (state.isAir())
                continue;
            if (BlockRendererDispatcher.shouldRender(state)) {
                extension.pollen_getBlockRenderPositions().add(pos.immutable());
                if (BlockRendererRegistry.get(state.getBlock()).stream().anyMatch(r -> r instanceof TickableBlockRenderer))
                    extension.pollen_getTickingBlockRenderPositions().add(pos.immutable());
            }
        }
    }
}
