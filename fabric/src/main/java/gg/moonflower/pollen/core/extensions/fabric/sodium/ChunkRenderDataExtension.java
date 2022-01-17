package gg.moonflower.pollen.core.extensions.fabric.sodium;

import net.minecraft.core.BlockPos;

import java.util.Set;

public interface ChunkRenderDataExtension {

    Set<BlockPos> pollen_getBlockRenderPositions();

    Set<BlockPos> pollen_getTickingBlockRenderPositions();
}
