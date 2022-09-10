package gg.moonflower.pollen.api.fluid.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

/**
 * Indicates a fluid has custom defined behavior.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedFluid {

    Direction[] DEFAULT_INTERACTIONS = new Direction[0];

    /**
     * @return The location of the still texture image
     */
    ResourceLocation getStillTextureName();

    /**
     * @return The location of the flowing texture image
     */
    ResourceLocation getFlowingTextureName();

    /**
     * @return The location of the overlay texture image
     */
    @Nullable
    default ResourceLocation getOverlayTextureName() {
        return null;
    }

    /**
     * @return The sound to play when this fluid is placed
     */
    default Optional<SoundEvent> getEmptySound() {
        return Optional.of(SoundEvents.BUCKET_EMPTY);
    }

    /**
     * @return The directions block interactions occur in
     */
    default Direction[] getInteractionDirections() {
        return DEFAULT_INTERACTIONS;
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

    /**
     * Plays an effect for when this fluid interacts with another block. By default, it does the water/lava hiss.
     *
     * @param level      The level the fluid is in
     * @param fluidState The state of the fluid
     * @param pos        The position of the fluid
     */
    default void playInteractionEffect(Level level, FluidState fluidState, BlockPos pos) {
        level.levelEvent(1501, pos, 0);
    }
}
