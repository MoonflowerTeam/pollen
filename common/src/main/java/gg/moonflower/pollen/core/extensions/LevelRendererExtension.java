package gg.moonflower.pollen.core.extensions;

import net.minecraft.core.BlockPos;

import java.util.stream.Stream;

public interface LevelRendererExtension {

    Stream<BlockPos> pollen_getBlockRenderers();

    Stream<BlockPos> pollen_getTickingBlockRenderers();
}
