package gg.moonflower.pollen.api.block;

import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * A wall sign block for modded signs.
 *
 * @author Jackson
 * @since 1.4.0
 */
public class PollinatedWallSignBlock extends WallSignBlock implements PollinatedSign {

    public PollinatedWallSignBlock(BlockBehaviour.Properties properties, WoodType woodType) {
        super(properties, woodType);
    }
}
