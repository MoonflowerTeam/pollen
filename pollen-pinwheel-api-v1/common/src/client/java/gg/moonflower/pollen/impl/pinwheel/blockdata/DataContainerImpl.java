package gg.moonflower.pollen.impl.pinwheel.blockdata;

import gg.moonflower.pollen.api.pinwheel.v1.blockdata.BlockData;
import gg.moonflower.pollen.api.pinwheel.v1.blockdata.BlockDataKey;
import gg.moonflower.pollen.api.pinwheel.v1.render.BlockRenderer;
import gg.moonflower.pollen.api.pinwheel.v1.render.BlockRendererTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class DataContainerImpl {

    private final BlockGetter level;
    private final BlockPos.MutableBlockPos pos;
    private final BlockRenderer.DataContainer container;

    public DataContainerImpl(BlockGetter level) {
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
        if (this.level instanceof BlockRendererTicker && !this.pos.equals(pos))
            ((BlockRendererTicker) this.level).scheduleBlockRendererTick(pos);
    }

    private <T> BlockData<T> get(BlockDataKey<T> key, BlockPos pos) {
        return BlockDataStorage.get(this.level).get(key, pos);
    }

    public BlockRenderer.DataContainer get(BlockPos pos) {
        this.pos.set(pos);
        return this.container;
    }
}
