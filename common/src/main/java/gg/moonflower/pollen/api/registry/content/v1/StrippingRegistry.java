package gg.moonflower.pollen.api.registry.content.v1;

import gg.moonflower.pollen.impl.registry.content.StrippingRegistryImpl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Registry for when a shovel right-clicks a block.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface StrippingRegistry {

    /**
     * Registers a stripping recipe.
     *
     * @param from The "log" block to turn into the other
     * @param to   The "stripped" block created from the "log"
     */
    static void register(Block from, Block to) {
        StrippingRegistryImpl.register(from, to);
    }

    /**
     * Retrieves a stripped state from the specified block state.
     *
     * @param state The state to strip
     * @return The stripped state or <code>null</code> if there is no registered strip
     */
    static @Nullable BlockState getStrippedState(BlockState state) {
        return StrippingRegistryImpl.getStrippedState(state);
    }
}
