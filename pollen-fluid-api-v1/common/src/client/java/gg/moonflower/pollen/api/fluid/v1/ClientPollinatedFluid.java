package gg.moonflower.pollen.api.fluid.v1;

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
public interface ClientPollinatedFluid {

    /**
     * @param camera       The current camera
     * @param level        The client level instance
     * @param biome        The biome the player is in
     * @param partialTicks The percentage from last tick to this tick
     * @return The color to apply for fog
     */
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
    default void applyFog(GameRenderer renderer, Camera camera, float distance, float partialTicks) {
        Entity entity = camera.getEntity();

        float g = 192.0F;
        if (entity instanceof LocalPlayer) {
            LocalPlayer localPlayer = (LocalPlayer) entity;
            g *= Math.max(0.25F, localPlayer.getWaterVision());
            Holder<Biome> holder = localPlayer.level.getBiome(localPlayer.blockPosition());
            if (Biome.getBiomeCategory(holder) == Biome.BiomeCategory.SWAMP) {
                g *= 0.85F;
            }
        }

        RenderSystem.setShaderFogStart(-8.0F);
        RenderSystem.setShaderFogEnd(g * 0.5F);
    }
}
