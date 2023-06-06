package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.pollen.api.animation.v1.state.AnimationState;

/**
 * Listens to animation state changes in {@link StateAnimationController}.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface AnimationStateListener {

    /**
     * Called when the specified animation starts playing.
     *
     * @param state The state that is now playing
     */
    default void onAnimationStart(AnimationState state) {
    }

    /**
     * Called when the specified animation stops playing.
     *
     * @param state The state that is no longer playing
     */
    default void onAnimationStop(AnimationState state) {
    }

    /**
     * Called when there are now no more animations playing.
     */
    default void onAnimationsComplete() {
    }
}
