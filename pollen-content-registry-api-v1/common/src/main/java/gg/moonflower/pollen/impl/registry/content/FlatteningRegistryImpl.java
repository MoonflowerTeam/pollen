package gg.moonflower.pollen.impl.registry.content;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class FlatteningRegistryImpl {

    private static final Map<Block, BlockState> REGISTRY = new ConcurrentHashMap<>();

    private FlatteningRegistryImpl() {
    }

    public static void register(Block from, BlockState to) {
        REGISTRY.put(from, to);
    }

    @Nullable
    public static BlockState getFlattenedState(BlockState state) {
        return REGISTRY.get(state.getBlock());
    }
}
