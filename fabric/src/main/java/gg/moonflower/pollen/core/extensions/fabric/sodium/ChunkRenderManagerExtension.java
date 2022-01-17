package gg.moonflower.pollen.core.extensions.fabric.sodium;

import net.minecraft.core.BlockPos;

import java.util.stream.Stream;

public interface ChunkRenderManagerExtension {

    Stream<BlockPos> pollen_getBlockRenderPositions();

    Stream<BlockPos> pollen_getTickingBlockRenderPositions();
}
