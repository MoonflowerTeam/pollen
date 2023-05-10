package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.pinwheel.api.animation.AnimationController;

public interface PollenAnimationController extends AnimationController {

    /**
     * Steps forward one tick (0.05 seconds) for all animations.
     */
    void tick();
}
