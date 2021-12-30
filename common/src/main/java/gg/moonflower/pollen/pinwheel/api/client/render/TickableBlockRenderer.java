package gg.moonflower.pollen.pinwheel.api.client.render;

import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Specifies a block renderer needs to be ticked in addition to rendering.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface TickableBlockRenderer extends BlockRenderer {

    /**
     * Called each level tick for each block in a chunk with a renderer.
     *
     * @param level     The level to tick in
     * @param pos       The position of the block to tick
     * @param container The container for retrieving {@link BlockData}
     */
    void tick(Level level, BlockPos pos, DataContainer container);
}
