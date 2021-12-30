package gg.moonflower.pollen.core.extensions;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.stream.Stream;

public interface LevelRendererExtensions {

    Stream<BlockPos> pollen_getBlockRenderers();
}
