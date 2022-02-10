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
    public interface SetupDensity {

        /**
         * Sets up the fog density in the renderer.
         *
         * @param renderer     The renderer instance
         * @param camera       The camera instance
         * @param context      The setter for fog values
         * @param distance     The expected far-plane of the fog
         * @param partialTicks The percentage from last tick to this tick
         */
        void setupFogDensity(GameRenderer renderer, Camera camera, FogContext context, float distance, float partialTicks);

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

    /**
     * Context for customizing fog during the density phase.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public interface FogContext {

        /**
         * Enabled fog rendering.
         */
        void enableFog();

        /**
         * Disables fog rendering.
         */
        void disableFog();

        /**
         * Sets the mode for fog rendering.
         *
         * @param glMode The mode of rendering. One of:<br><table><tr><td>{@link org.lwjgl.opengl.GL11#GL_EXP EXP}</td><td>{@link org.lwjgl.opengl.GL11#GL_EXP2 EXP2}</td><td>{@link org.lwjgl.opengl.GL11#GL_LINEAR LINEAR}</td><td>{@link org.lwjgl.opengl.GL14#GL_FRAGMENT_DEPTH FRAGMENT_DEPTH}</td><td>{@link org.lwjgl.opengl.GL15#GL_FOG_COORD FOG_COORD}</td></tr></table>
         */
        void fogMode(int glMode);

        /**
         * Sets the fog density value.
         *
         * @param density The new value
         */
        void fogDensity(float density);

        /**
         * Sets the near plane for fog to start at from the camera.
         *
         * @param nearPlane The new value
         */
        void fogStart(float nearPlane);

        /**
         * Sets the far plane for fog to end at from the camera.
         *
         * @param farPlane The new value
         */
        void fogEnd(float farPlane);

        /**
         * Sets up normal fog distance.
         */
        void setupNvFogDistance();
    }
}
