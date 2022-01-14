package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.core.extensions.CompiledChunkExtension;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

import java.util.HashSet;
import java.util.Set;

@Mixin(ChunkRenderDispatcher.CompiledChunk.class)
public class CompiledChunkMixin implements CompiledChunkExtension {

    private final Set<BlockPos> renderableBlocks = new HashSet<>();

    @Override
    public Set<BlockPos> pollen_getBlockRenderPositions() {
        return this.renderableBlocks;
    }
}
