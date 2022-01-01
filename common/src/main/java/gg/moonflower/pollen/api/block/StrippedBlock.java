package gg.moonflower.pollen.api.block;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Enables custom copying of properties from a "log" state into a "stripped log" state. By default, all compatible properties are copied. Only use this interface if some properties should not be shared.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface StrippedBlock {

    /**
     * Copies the properties from the specified state into this state.
     *
     * @param state The state to copy values from
     * @return This state with all copied properties from the provided state
     */
    BlockState copyStrippedPropertiesFrom(BlockState state);
}
