package gg.moonflower.pollen.api.fluid;

import gg.moonflower.pollen.api.event.events.client.render.FogEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

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
     * @param camera       The current camera
     * @param level        The client level instance
     * @param biome        The biome the player is in
     * @param partialTicks The percentage from last tick to this tick
     * @return The color to apply for fog
     */
    @Environment(EnvType.CLIENT)
    default int getFogColor(Camera camera, ClientLevel level, Biome biome, float partialTicks) {
        return biome.getWaterFogColor();
    }

    /**
     * Applies fog effects to the game while in this fluid.
     *
     * @param renderer     The renderer instance
     * @param camera       The camera instance
     * @param context      The setter for fog values
     * @param distance     The expected far-plane of the fog
     * @param partialTicks The percentage from last tick to this tick
     */
    @Environment(EnvType.CLIENT)
    default void applyFog(GameRenderer renderer, Camera camera, FogEvents.FogContext context, float distance, float partialTicks) {
        Entity entity = camera.getEntity();

        float f = 0.05F;
        if (entity instanceof LocalPlayer) {
            LocalPlayer localPlayer = (LocalPlayer) entity;
            f -= localPlayer.getWaterVision() * localPlayer.getWaterVision() * 0.03F;
            Biome biome = localPlayer.level.getBiome(localPlayer.blockPosition());
            if (biome.getBiomeCategory() == Biome.BiomeCategory.SWAMP) {
                f += 0.005F;
            }
        }

        context.fogDensity(f);
        context.fogMode(GL11.GL_EXP2);
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
}
