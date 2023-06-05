package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.pinwheel.api.animation.AnimationController;

/**
 * An abstract pollen animation controller.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface PollenAnimationController extends AnimationController {

    /**
     * Steps forward one tick (0.05 seconds) for all animations.
     */
    void tick();

    /**
     * Sets the lifetime of the controller.
     *
     * @param lifetime The new lifetime value
     */
    void setLifetime(float lifetime);

    /**
     * Sets all render parameters.
     *
     * @param xRotation       The x rotation of the head (yaw)
     * @param yRotation       The y rotation of the head (pitch)
     * @param limbSwing       The limb swing factor
     * @param limbSwingAmount The limb swing amount factor
     */
    void setRenderParameters(float xRotation, float yRotation, float limbSwing, float limbSwingAmount);
}
