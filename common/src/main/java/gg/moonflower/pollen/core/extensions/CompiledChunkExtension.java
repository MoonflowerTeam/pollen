package gg.moonflower.pollen.core.extensions;

import net.minecraft.core.BlockPos;

import java.util.Set;

public interface CompiledChunkExtension {

    Set<BlockPos> pollen_getBlockRenderers();

    Set<BlockPos> pollen_getTickingBlockRenderers();
}
