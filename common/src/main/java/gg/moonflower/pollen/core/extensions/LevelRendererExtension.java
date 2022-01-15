package gg.moonflower.pollen.core.extensions;

import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

import java.util.stream.Stream;

public interface LevelRendererExtension {

    BlockRenderer.DataContainer pollen_getDataContainer(ClientLevel level, BlockPos pos);

    Stream<BlockPos> pollen_getBlockRenderers();
}
