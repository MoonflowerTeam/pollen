package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.pollen.api.animation.v1.state.AnimationState;

import java.util.Collection;

/**
 * An animation controller that sets animations based on {@linkplain AnimationState animation states}.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface StateAnimationController extends PollenAnimationController {

    /**
     * Clears all playing animations.
     */
    void clearAnimations();

    /**
     * Adds the specified listener for animation events.
     *
     * @param listener The listener to add
     */
    void addListener(AnimationStateListener listener);

    /**
     * Removes the specified listener for animation events.
     *
     * @param listener The listener to remove
     */
    void removeListener(AnimationStateListener listener);

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
        if (this.isAnimationPlaying(animation)) {
            return;
        }

        this.clearAnimations();
        this.startAnimations(animation);
    }

    /**
     * @return All animation states playing in this controller
     */
    Collection<AnimationState> getPlayingStates();
}
