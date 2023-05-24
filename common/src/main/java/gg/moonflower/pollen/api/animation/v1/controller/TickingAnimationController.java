package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.pinwheel.api.animation.AnimationController;

/**
 * An animation controller that requires ticking.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface TickingAnimationController extends AnimationController {

    /**
     * Steps forward one tick (0.05 seconds) for all animations.
     */
    void tick();
}
