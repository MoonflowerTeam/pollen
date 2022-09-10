package gg.moonflower.pollen.api.registry.v1.block;

import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * A standing sign block for modded signs.
 *
 * @author Jackson
 * @since 1.4.0
 */
public class PollinatedStandingSignBlock extends StandingSignBlock implements PollinatedSign {

    public PollinatedStandingSignBlock(Properties properties, WoodType woodType) {
        super(properties, woodType);
    }
}
