package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pollen.api.animation.v1.RenderAnimationTimer;
import gg.moonflower.pollen.impl.animation.PollenPlayingAnimationImpl;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Updates the interpolated render time for all animations in the controller.
     *
     * @param partialTicks The percentage from last tick to this tick
     */
    default void updateRenderTime(float partialTicks) {
        for (PlayingAnimation animation : this.getPlayingAnimations()) {
            if (animation instanceof PollenPlayingAnimationImpl impl) {
                impl.setRenderTime(partialTicks);
            }
        }
    }

    /**
     * Sets a custom timing scheme that runs how the specified animation will calculate render time.
     *
     * @param animation The animation to set a custom timer for
     * @param timer     The timer to use
     */
    void setRenderTimer(ResourceLocation animation, @Nullable RenderAnimationTimer timer);

    /**
     * Retrieves the render timer the specified animation uses.
     *
     * @param animation The animation to get the render timer for
     * @return The timer that animation uses
     */
    RenderAnimationTimer getRenderTimer(ResourceLocation animation);
}
