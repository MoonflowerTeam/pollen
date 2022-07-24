package gg.moonflower.pollen.api.fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
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

    Direction[] DEFAULT_INTERACTIONS = Arrays.stream(Direction.values()).filter(direction -> direction != Direction.DOWN).toArray(Direction[]::new);

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
     * @param camera       The current camera
     * @param level        The client level instance
     * @param biome        The biome the player is in
     * @param partialTicks The percentage from last tick to this tick
     * @return The color to apply for fog
     */
    @Environment(EnvType.CLIENT)
    default int getFogColor(Camera camera, ClientLevel level, Holder<Biome> biome, float partialTicks) {
        return biome.value().getWaterFogColor();
    }

    /**
     * Applies fog effects to the game while in this fluid.
     *
     * @param renderer     The renderer instance
     * @param camera       The camera instance
     * @param distance     The expected far-plane of the fog
     * @param partialTicks The percentage from last tick to this tick
     */
    @Environment(EnvType.CLIENT)
    default void applyFog(GameRenderer renderer, Camera camera, float distance, float partialTicks) {
        Entity entity = camera.getEntity();

        float g = 192.0F;
        if (entity instanceof LocalPlayer) {
            LocalPlayer localPlayer = (LocalPlayer) entity;
            g *= Math.max(0.25F, localPlayer.getWaterVision());
            Holder<Biome> holder = localPlayer.level.getBiome(localPlayer.blockPosition());
            if (holder.is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                g *= 0.85F;
            }
        }

        RenderSystem.setShaderFogStart(-8.0F);
        RenderSystem.setShaderFogEnd(g * 0.5F);
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
