package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pollen.api.animation.v1.state.AnimationState;

/**
 * An animation controller that sets animations based on {@linkplain AnimationState animation states}.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface StateAnimationController extends AnimationController {

    /**
     * Clears all playing animations.
     */
    void clearAnimations();

    /**
     * Starts all animations in the specified state.
     *
     * @param animation The animation state to start playing
     * @return Whether there was an animation state change
     */
    boolean startAnimations(AnimationState animation);

    /**
     * Stops all animations in the specified state.
     *
     * @param animation The animation state to stop playing
     * @return Whether there was an animation state change
     */
    boolean stopAnimations(AnimationState animation);

    /**
     * Checks if the specified animation state is playing.
     *
     * @param animation The state to check for
     * @return Whether the state is currently playing
     */
    boolean isAnimationPlaying(AnimationState animation);

    /**
     * Stops all current animations and sets the current animation state to the specified value.
     *
     * @param animation The animation state to start playing
     */
    default void setPlayingAnimation(AnimationState animation) {
        this.clearAnimations();
        this.startAnimations(animation);
    }
}
