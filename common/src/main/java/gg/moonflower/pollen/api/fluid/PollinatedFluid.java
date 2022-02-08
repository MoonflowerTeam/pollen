package gg.moonflower.pollen.api.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Indicates a fluid has custom defined behavior.
 *
 * @author Ocelot
 */
public interface PollinatedFluid {

    ResourceLocation getStillTextureName();

    ResourceLocation getFlowingTextureName();

    default Direction[] getInteractionDirections() {
        return Arrays.stream(Direction.values()).filter(direction -> direction != Direction.DOWN).toArray(Direction[]::new);
    }

    /**
     * Retrieves a custom interaction between this fluid and another block.
     *
     * @param level       The level to check for interactions
     * @param fluidState  The state of this fluid
     * @param pos         The position of this fluid
     * @param neighborPos The position to check an interaction at
     * @return The state to transform into or <code>null</code> for no interaction
     */
    @Nullable
    default BlockState getInteractionState(Level level, FluidState fluidState, BlockPos pos, BlockPos neighborPos) {
        return null;
    }
}
