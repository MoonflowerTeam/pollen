package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pollen.api.animation.v1.state.AnimationState;

public interface StateAnimationController extends AnimationController {

    void clearAnimations();

    boolean startAnimations(AnimationState animation);

    boolean stopAnimations(AnimationState animation);

    boolean isAnimationPlaying(AnimationState animation);

    default void setPlayingAnimation(AnimationState animation) {
        this.clearAnimations();
        this.startAnimations(animation);
    }
}
