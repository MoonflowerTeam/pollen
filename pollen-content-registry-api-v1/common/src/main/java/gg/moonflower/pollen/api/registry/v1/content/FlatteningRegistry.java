package gg.moonflower.pollen.api.registry.v1.content;

import gg.moonflower.pollen.impl.registry.content.FlatteningRegistryImpl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Registry for when a shovel right-clicks a block.
 *
 * @author Ocelot
 * @since 1.5.0
 */
public interface FlatteningRegistry {

    /**
     * Registers a flattening recipe (shovel recipe).
     *
     * @param from The "log" block to turn into the other
     * @param to   The "stripped" block created from the "log"
     */
    static void register(Block from, BlockState to) {
        FlatteningRegistryImpl.register(from, to);
    }

    /**
     * Retrieves a stripped state from the specified block state.
     *
     * @param state The state to strip
     * @return The stripped state or <code>null</code> if there is no registered strip
     */
    @Nullable
    static BlockState getFlattenedState(BlockState state) {
        return FlatteningRegistryImpl.getFlattenedState(state);
    }
}
