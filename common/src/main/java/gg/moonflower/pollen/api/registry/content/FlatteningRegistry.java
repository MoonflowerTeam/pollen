package gg.moonflower.pollen.api.registry.content;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for when a shovel right-clicks a block.
 *
 * @author Ocelot
 * @since 1.5.0
 */
public final class FlatteningRegistry {

    private static final Map<Block, BlockState> REGISTRY = new ConcurrentHashMap<>();

    private FlatteningRegistry() {
    }

    /**
     * Registers a flattening recipe (shovel recipe).
     *
     * @param from The "log" block to turn into the other
     * @param to   The "stripped" block created from the "log"
     */
    public static void register(Block from, BlockState to) {
        REGISTRY.put(from, to);
    }

    /**
     * Retrieves a stripped state from the specified block state.
     *
     * @param state The state to strip
     * @return The stripped state or <code>null</code> if there is no registered strip
     */
    @Nullable
    public static BlockState getFlattenedState(BlockState state) {
        return REGISTRY.get(state.getBlock());
    }
}
