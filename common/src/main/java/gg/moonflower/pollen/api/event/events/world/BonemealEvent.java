package gg.moonflower.pollen.api.event.events.world;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.event.ResultContext;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Fired when a block is bonemealed.
 *
 * @author ebo2022
 * @since 2.0.0
 */
@FunctionalInterface
public interface BonemealEvent {
    PollinatedEvent<BonemealEvent> EVENT = EventRegistry.createCancellable(BonemealEvent.class);

    /**
     * Called when the specified block is about to be bonemealed.
     *
     * @param level   The level
     * @param pos     The position of the block being bonemealed
     * @param state   The BlockState of the block being bonemealed
     * @param stack   The ItemStack of bonemeal
     * @param context Context for setting the result of this event
     * @return <code>true</code> to continue processing the event, or <code>false</code> to cancel it
     */
    boolean bonemeal(Level level, BlockPos pos, BlockState state, ItemStack stack, ResultContext context);
}
