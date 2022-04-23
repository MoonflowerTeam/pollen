package gg.moonflower.pollen.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * A class whose instance can store a {@link BlockPos} and {@link Direction}.
 *
 * @author JustinPlayzz
 * @author Steven
 * @author ebo2022
 */
public class DirectionalBlockPos {
    public BlockPos pos;
    public Direction direction;

    public DirectionalBlockPos(BlockPos p, Direction a) {
        pos = p;
        direction = a;
    }
}
