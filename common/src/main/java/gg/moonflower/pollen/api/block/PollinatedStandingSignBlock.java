package gg.moonflower.pollen.api.block;

import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * A standing sign block for modded signs.
 *
 * @author Jackson
 * @since 1.4.0
 */
public class PollinatedStandingSignBlock extends StandingSignBlock implements PollinatedSign {

    public PollinatedStandingSignBlock(BlockBehaviour.Properties properties, WoodType woodType) {
        super(properties, woodType);
    }
}
