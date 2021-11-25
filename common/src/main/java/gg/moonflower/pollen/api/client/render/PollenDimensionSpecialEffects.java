package gg.moonflower.pollen.api.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.jetbrains.annotations.Nullable;

/**
 * Enables custom {@link net.minecraft.client.renderer.DimensionSpecialEffects} to also have custom renderers for hardcoded effects.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollenDimensionSpecialEffects {

    /**
     * @return The renderer for clouds or <code>null</code> to use default
     */
    @Nullable
    default Renderer getCloudRenderer() {
        return null;
    }

    /**
     * @return The renderer for weather or <code>null</code> to use default
     */
    @Nullable
    default Renderer getWeatherRenderer() {
        return null;
    }

    /**
     * @return The renderer for weather particles or <code>null</code> to use default
     */
    @Nullable
    default Renderer getWeatherParticleRenderer() {
        return null;
    }

    /**
     * @return The renderer for the sky or <code>null</code> to use default
     */
    @Nullable
    default Renderer getSkyRenderer() {
        return null;
    }

    /**
     * Draws a custom implementation of rendering for a dimension.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    interface Renderer {
        void render(RenderContext context);
    }

    /**
     * Context for rendering in {@link Renderer}.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    interface RenderContext {

        /**
         * @return The client tick count of the renderer. This should be used to tick renderers
         */
        int getTicks();

        /**
         * @return The percentage from last tick to this tick
         */
        float getPartialTicks();

        /**
         * @return The rendering camera instance
         */
        Camera getCamera();

        /**
         * @return A stack of current matrix transforms
         */
        PoseStack getMatrixStack();

        /**
         * @return The projection matrix for current rendering
         */
        Matrix4f getProjection();

        /**
         * @return The client level instance
         */
        ClientLevel getLevel();

        /**
         * @return The Minecraft game renderer instance
         */
        default GameRenderer getGameRenderer() {
            return Minecraft.getInstance().gameRenderer;
        }

        /**
         * @return The Minecraft level renderer instance
         */
        default LevelRenderer getLevelRenderer() {
            return Minecraft.getInstance().levelRenderer;
        }

        /**
         * @return The game renderer light map texture
         */
        default LightTexture getLightMap() {
            return this.getGameRenderer().lightTexture();
        }
    }
}
