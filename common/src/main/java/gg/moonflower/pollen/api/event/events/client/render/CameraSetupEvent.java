package gg.moonflower.pollen.api.event.events.client.render;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

/**
 * Called each time the camera is set up in the scene.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface CameraSetupEvent {

    PollinatedEvent<CameraSetupEvent> EVENT = EventRegistry.createLoop(CameraSetupEvent.class);

    /**
     * Sets up the camera in the renderer.
     *
     * @param renderer     The renderer instance
     * @param camera       The camera instance
     * @param setter       The setter for camera values
     * @param partialTicks The percentage from last tick to this tick
     */
    void setupCamera(GameRenderer renderer, Camera camera, CameraSetter setter, float partialTicks);

    /**
     * Allows the getting and setting of values in the camera.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    interface CameraSetter {

        /**
         * @return The camera pitch
         */
        float getXRotation();

        /**
         * @return The camera yaw
         */
        float getYRotation();

        /**
         * @return The camera roll
         */
        float getZRotation();

        /**
         * Sets the camera pitch.
         *
         * @param pitch The new X rotation
         */
        void setXRotation(float pitch);

        /**
         * Sets the camera yaw.
         *
         * @param yaw The new Y rotation
         */
        void setYRotation(float yaw);

        /**
         * Sets the camera roll.
         *
         * @param roll The new Z rotation
         */
        void setZRotation(float roll);
    }
}
