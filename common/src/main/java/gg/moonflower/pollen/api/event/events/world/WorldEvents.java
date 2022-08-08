package gg.moonflower.pollen.api.event.events.world;

import gg.moonflower.pollen.api.event.EventResult;
import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public final class WorldEvents {

    public static final PollinatedEvent<Bonemeal> BONEMEAL = EventRegistry.createEventResult(Bonemeal.class);
    public static final PollinatedEvent<TreeGrowing> TREE_GROWING = EventRegistry.createEventResult(TreeGrowing.class);

    private WorldEvents() {
    }

    /**
     * Fired when a block is bonemealed.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface Bonemeal {

        /**
         * Called when the specified block is about to be bonemealed.
         *
         * @param level   The level
         * @param pos     The position of the block being bonemealed
         * @param state   The block being bonemealed
         * @param stack   The bonemeal ItemStack in the player's hand
         * @return The result for this event. {@link EventResult#PASS} continues onto the next listener, while any others will override vanilla behavior
         */
        EventResult bonemeal(Level level, BlockPos pos, BlockState state, ItemStack stack);
    }

    /**
     * Fired when a sapling grows into a tree.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface TreeGrowing {

        /**
         * Called when a {@link SaplingBlock} is successfully grown.
         *
         * @param level The level the sapling is in
         * @param rand  An instance of {@link Random} for use in code
         * @param pos   The origin position of the sapling
         * @return The result for this event. {@link EventResult#PASS} continues onto the next listener, while any others will override vanilla behavior
         */
        EventResult interaction(LevelAccessor level, Random rand, BlockPos pos);
    }
}
