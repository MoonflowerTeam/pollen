package gg.moonflower.pollen.api.event.events.client.render;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

public final class FogEvents {

    public static final PollinatedEvent<SetupColor> FOG_COLOR = EventRegistry.createLoop(SetupColor.class);
    public static final PollinatedEvent<SetupDensity> FOG_DENSITY = EventRegistry.createLoop(SetupDensity.class);

    private FogEvents() {
    }

    /**
     * Called each time fog colors are calculated.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface SetupColor {

        /**
         * Sets up the fog colors in the renderer.
         *
         * @param renderer     The renderer instance
         * @param camera       The camera instance
         * @param context      The setter for camera values
         * @param partialTicks The percentage from last tick to this tick
         */
        void setupFogColors(GameRenderer renderer, Camera camera, ColorContext context, float partialTicks);

    }

    /**
     * Called each time the fog is set up in the scene.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface SetupDensity {

        /**
         * Sets up the fog density in the renderer.
         *
         * @param renderer     The renderer instance
         * @param camera       The camera instance
         * @param distance     The expected far-plane of the fog
         * @param partialTicks The percentage from last tick to this tick
         */
        void setupFogDensity(GameRenderer renderer, Camera camera, float distance, float partialTicks);

    }

    /**
     * Allows the getting and setting of fog colors.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public interface ColorContext {

        /**
         * @return The red component of the fog
         */
        float getRed();

        /**
         * @return The green component of the fog
         */
        float getGreen();

        /**
         * @return The blue component of the fog
         */
        float getBlue();

        /**
         * Sets the red component of the fog.
         *
         * @param red The new red value
         */
        void setRed(float red);

        /**
         * Sets the green component of the fog.
         *
         * @param green The green red value
         */
        void setGreen(float green);

        /**
         * Sets the blue component of the fog.
         *
         * @param blue The new blue value
         */
        void setBlue(float blue);
    }
}
