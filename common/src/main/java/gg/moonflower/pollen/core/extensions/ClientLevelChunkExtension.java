package gg.moonflower.pollen.core.extensions;

import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.Set;

public interface ClientLevelChunkExtension {

    Map<BlockPos, Set<TickableBlockRenderer>> pollen_getTickableBlockRenderers();
}
