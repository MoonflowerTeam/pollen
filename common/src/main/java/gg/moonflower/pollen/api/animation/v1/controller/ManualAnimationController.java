package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import org.jetbrains.annotations.Nullable;

public interface ManualAnimationController extends PollenAnimationController {

    void clearAnimations();

    PlayingAnimation addAnimation(AnimationData animation) throws IllegalArgumentException;

    boolean removeAnimation(AnimationData animation);

    @Nullable PlayingAnimation getPlayingAnimation(AnimationData animation);

    default boolean isAnimationPlaying(AnimationData animation) {
        return this.getPlayingAnimation(animation) != null;
    }

    default PlayingAnimation setPlayingAnimation(AnimationData animation) {
        this.clearAnimations();
        return this.addAnimation(animation);
    }
}
