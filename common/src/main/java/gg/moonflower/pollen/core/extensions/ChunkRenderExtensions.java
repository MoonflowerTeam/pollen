package gg.moonflower.pollen.core.extensions;

import net.minecraft.core.BlockPos;

import java.util.Set;

public interface ChunkRenderExtensions {

    Set<BlockPos> pollen_getBlockRenderPositions();

    Set<BlockPos> pollen_getTickingBlockRenderers();
}
