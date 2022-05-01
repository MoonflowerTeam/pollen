package gg.moonflower.pollen.pinwheel.core.client;

import gg.moonflower.pollen.api.event.events.network.ClientNetworkEvents;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockData;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockDataKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class BlockDataStorage {

    private static final Map<BlockGetter, BlockDataStorage> DATA = new WeakHashMap<>(3); // There are only 3 vanilla dimensions so only expand if mods add more
    private final BlockGetter level;
    private final Map<BlockPos, Map<BlockDataKey<?>, BlockData<?>>> data;

    static {
        ClientNetworkEvents.LOGOUT.register((controller, player, connection) -> DATA.clear());
    }

    private BlockDataStorage(BlockGetter level) {
        this.level = level;
        this.data = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> BlockData<T> get(BlockDataKey<T> key, BlockPos pos) {
        BlockState state = this.level.getBlockState(pos);
        Map<BlockDataKey<?>, BlockData<?>> map = this.data.computeIfAbsent(pos.immutable(), __ -> new HashMap<>());
        if (map.containsKey(key) && !key.getFilter().test(state))
            map.remove(key);
        return (BlockData<T>) map.computeIfAbsent(key, BlockDataImpl::new);
    }

    public void invalidateChunk(ChunkPos chunkPos) {
        this.data.keySet().removeIf(pos -> pos.getX() >= chunkPos.getMinBlockX() && pos.getX() < chunkPos.getMaxBlockX() && pos.getZ() >= chunkPos.getMinBlockZ() && pos.getZ() < chunkPos.getMaxBlockZ());
    }

    public static BlockDataStorage get(BlockGetter level) {
        return DATA.computeIfAbsent(level, BlockDataStorage::new);
    }

    public void update(BlockState state, BlockPos pos) {
        if (!this.data.containsKey(pos))
            return;
        Map<BlockDataKey<?>, BlockData<?>> map = this.data.get(pos);
        map.keySet().removeIf(key -> !key.getFilter().test(state));
    }

    private static class BlockDataImpl<T> implements BlockData<T> {

        private final Supplier<T> defaultValue;
        private T value;

        private BlockDataImpl(BlockDataKey<T> key) {
            this.defaultValue = key.getDefault();
            this.value = key.getDefault().get();
        }

        @Override
        public void set(@Nullable T value) {
            this.value = value != null ? value : this.defaultValue.get();
        }

        @Override
        public T get() {
            return value;
        }
    }
}
