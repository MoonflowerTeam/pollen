package gg.moonflower.pollen.core.mixin.client;

import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkRenderDispatcher.RenderChunk.class)
public interface ChunkRenderDispatcherRenderChunkAccessor {

    @Accessor
    BlockPos.MutableBlockPos getOrigin();
}
