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

    @Override
    default void clearAnimations() {
        this.clearAnimations(0);
    }

    /**
     * Clears all playing animations over the span of the specified ticks.
     *
     * @param transitionTicks The number of ticks to take when transitioning out
     */
    void clearAnimations(int transitionTicks);

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
    default boolean startAnimations(AnimationState animation) {
        return this.startAnimations(animation, 3);
    }

    /**
     * Starts all animations in the specified state.
     *
     * @param animation       The animation state to start playing
     * @param transitionTicks The number of ticks to transition into the animations
     * @return Whether there was an animation state change
     */
    boolean startAnimations(AnimationState animation, int transitionTicks);

    /**
     * Stops all animations in the specified state.
     *
     * @param animation The animation state to stop playing
     * @return Whether there was an animation state change
     */
    default boolean stopAnimations(AnimationState animation) {
        return this.stopAnimations(animation, 3);
    }

    /**
     * Stops all animations in the specified state.
     *
     * @param animation       The animation state to stop playing
     * @param transitionTicks The number of ticks to transition out of the animations
     * @return Whether there was an animation state change
     */
    boolean stopAnimations(AnimationState animation, int transitionTicks);

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
        this.setPlayingAnimation(animation, 3);
    }

    /**
     * Stops all current animations and sets the current animation state to the specified value.
     *
     * @param animation       The animation state to start playing
     * @param transitionTicks The number of ticks to transition into the animations
     */
    default void setPlayingAnimation(AnimationState animation, int transitionTicks) {
        if (animation == AnimationState.EMPTY) {
            this.clearAnimations(transitionTicks);
            return;
        }

        if (this.isAnimationPlaying(animation)) {
            return;
        }

        this.clearAnimations(transitionTicks);
        this.startAnimations(animation, transitionTicks);
    }

    /**
     * @return All animation states playing in this controller
     */
    Collection<AnimationState> getPlayingStates();
}
