package gg.moonflower.pollen.api.animation.v1.controller;

import net.minecraft.resources.ResourceLocation;

/**
 * An animation controller that has default idle animations when no other animation is playing.
 *
 * @since 2.0.0
 */
public interface IdleAnimationController extends PollenAnimationController {

    /**
     * @return The animations to use while idle
     */
    ResourceLocation[] getIdleAnimations();

    /**
     * Sets the animation uses while idle.
     *
     * @param animations The new idle animations to play
     */
    void setIdleAnimations(ResourceLocation... animations);
}
