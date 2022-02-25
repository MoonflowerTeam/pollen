package gg.moonflower.pollen.pinwheel.api.client.render;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Ticks block renderers when updates are scheduled.
 *
 * @author Ocelot
 * @since 1.1.0
 */
public interface BlockRendererTicker extends BlockGetter {

    /**
     * Schedules an update at the specified position.
     *
     * @param pos The position of the block
     */
    default void scheduleBlockRendererTick(BlockPos pos) {
        this.scheduleBlockRendererTick(pos, this.getBlockState(pos));
    }

    /**
     * Schedules an update at the specified position with specified state.
     *
     * @param pos   The position of the block
     * @param state The state of the block
     */
    void scheduleBlockRendererTick(BlockPos pos, BlockState state);
}
