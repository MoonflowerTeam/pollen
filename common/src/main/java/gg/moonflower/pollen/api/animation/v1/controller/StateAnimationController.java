package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.pollen.api.animation.v1.state.AnimationState;

public interface StateAnimationController extends PollenAnimationController {

    void clearAnimations();

    void startAnimations(AnimationState animation);

    boolean stopAnimations(AnimationState animation);

    boolean isAnimationPlaying(AnimationState animation);

    default void setPlayingAnimation(AnimationState animation) {
        this.clearAnimations();
        this.startAnimations(animation);
    }
}
