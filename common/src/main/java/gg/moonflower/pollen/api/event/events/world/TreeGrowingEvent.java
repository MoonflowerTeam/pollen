package gg.moonflower.pollen.api.event.events.world;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SaplingBlock;

import java.util.Random;

/**
 * Fired when a {@link SaplingBlock} is growing into a tree.
 * @author ebo2022
 * @since 2.0.0
 */
@FunctionalInterface
public interface TreeGrowingEvent {
    PollinatedEvent<TreeGrowingEvent> EVENT = EventRegistry.createLoop(TreeGrowingEvent.class);

    /**
     * Called when a {@link SaplingBlock} attempts to grow or advance its stage.
     * @param pos   The origin position of the sapling
     * @param rand  An instance of {@link Random}
     * @param level An instance of {@link LevelAccessor} for the current level
     */
    void onTreeGrowing(BlockPos pos, Random rand, LevelAccessor level);
}
