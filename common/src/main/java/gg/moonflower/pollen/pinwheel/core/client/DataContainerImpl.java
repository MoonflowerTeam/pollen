package gg.moonflower.pollen.pinwheel.core.client;

import gg.moonflower.pollen.core.extensions.ClientLevelExtension;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockData;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockDataKey;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class DataContainerImpl {

    private final ClientLevel level;
    private final BlockPos.MutableBlockPos pos;
    private final BlockRenderer.DataContainer container;

    public DataContainerImpl(ClientLevel level) {
        this.level = level;
        this.pos = new BlockPos.MutableBlockPos();
        this.container = new BlockRenderer.DataContainer() {
            @Override
            public void updateNeighbor(Direction direction) {
                DataContainerImpl.this.scheduleTick(pos.relative(direction));
            }

            @Override
            public <T> BlockData<T> get(BlockDataKey<T> key) {
                return DataContainerImpl.this.get(key, pos);
            }

            @Override
            public <T> BlockData<T> get(BlockDataKey<T> key, BlockPos pos) {
                return DataContainerImpl.this.get(key, pos);
            }
        };
    }

    private void scheduleTick(BlockPos pos) {
        if (!this.pos.equals(pos))
            ((ClientLevelExtension) this.level).pollen_scheduleTick(pos);
    }

    private <T> BlockData<T> get(BlockDataKey<T> key, BlockPos pos) {
        return BlockDataStorage.get(this.level).get(key, pos);
    }

    public BlockRenderer.DataContainer get(BlockPos pos) {
        this.pos.set(pos);
        return this.container;
    }

    public Level getLevel() {
        return level;
    }
}
