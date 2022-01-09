package gg.moonflower.pollen.core.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface ClientLevelExtension {

    void pollen_scheduleTick(BlockPos pos);

    void pollen_scheduleTick(BlockPos pos, BlockState state);
}
