package gg.moonflower.pollen.api.registry;

import gg.moonflower.pollen.api.block.StrippedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO Move to `gg.moonflower.pollen.api.registry.content` in 2.0.0

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class StrippingRegistry {

    private static final Map<Block, Block> REGISTRY = new ConcurrentHashMap<>();

    private StrippingRegistry() {
    }

    /**
     * Registers a stripping recipe.
     *
     * @param from The "log" block to turn into the other
     * @param to   The "stripped" block created from the "log"
     */
    public static void register(Block from, Block to) {
        REGISTRY.put(from, to);
    }

    /**
     * Retrieves a stripped state from the specified block state.
     *
     * @param state The state to strip
     * @return The stripped state or <code>null</code> if there is no registered strip
     */
    @Nullable
    public static BlockState getStrippedState(BlockState state) {
        Block stripped = REGISTRY.get(state.getBlock());
        if (stripped == null)
            return null;

        BlockState strippedState = stripped.defaultBlockState();
        if (stripped instanceof StrippedBlock)
            return ((StrippedBlock) stripped).copyStrippedPropertiesFrom(state);
        for (Property<?> property : state.getProperties())
            if (strippedState.hasProperty(property))
                strippedState = genericsarestupidwtf(state, strippedState, property);
        return strippedState;
    }

    private static <T extends Comparable<T>> BlockState genericsarestupidwtf(BlockState from, BlockState to, Property<T> property) {
        return to.setValue(property, from.getValue(property));
    }
}
