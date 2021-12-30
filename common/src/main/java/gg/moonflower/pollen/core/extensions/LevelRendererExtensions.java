package gg.moonflower.pollen.core.extensions;

import net.minecraft.core.BlockPos;

import java.util.stream.Stream;

public interface LevelRendererExtensions {

    Stream<BlockPos> pollen_getBlockRenderers();
}
