package gg.moonflower.pollen.impl.registry.content;

import gg.moonflower.pollen.api.registry.content.v1.StrippedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public final class StrippingRegistryImpl {

    private static final Map<Block, Block> REGISTRY = new ConcurrentHashMap<>();

    private StrippingRegistryImpl() {
    }

    public static void register(Block from, Block to) {
        REGISTRY.put(from, to);
    }

    @Nullable
    public static BlockState getStrippedState(BlockState state) {
        Block stripped = REGISTRY.get(state.getBlock());
        if (stripped == null) {
            return null;
        }

        BlockState strippedState = stripped.defaultBlockState();
        if (stripped instanceof StrippedBlock) {
            return ((StrippedBlock) stripped).copyStrippedPropertiesFrom(state);
        }
        for (Property<?> property : state.getProperties()) {
            if (strippedState.hasProperty(property)) {
                strippedState = genericsarestupidwtf(state, strippedState, property);
            }
        }
        return strippedState;
    }

    private static <T extends Comparable<T>> BlockState genericsarestupidwtf(BlockState from, BlockState to, Property<T> property) {
        return to.setValue(property, from.getValue(property));
    }
}
