package gg.moonflower.pollen.impl.registry.content;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public final class FlatteningRegistryImpl {

    private static final Map<Block, BlockState> REGISTRY = new ConcurrentHashMap<>();

    private FlatteningRegistryImpl() {
    }

    public static void register(Block from, BlockState to) {
        REGISTRY.put(from, to);
    }

    public static @Nullable BlockState getFlattenedState(BlockState state) {
        return REGISTRY.get(state.getBlock());
    }
}
