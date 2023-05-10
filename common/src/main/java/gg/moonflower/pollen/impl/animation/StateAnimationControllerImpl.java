package gg.moonflower.pollen.impl.animation;

import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pollen.api.animation.v1.controller.StateAnimationController;
import gg.moonflower.pollen.api.animation.v1.state.AnimationState;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;

import java.util.Collection;

public class StateAnimationControllerImpl implements StateAnimationController {
    @Override
    public void tick() {

    }

    @Override
    public void clearAnimations() {

    }

    @Override
    public void startAnimations(AnimationState animation) {

    }

    @Override
    public boolean stopAnimations(AnimationState animation) {
        return false;
    }

    @Override
    public boolean isAnimationPlaying(AnimationState animation) {
        return false;
    }

    @Override
    public MolangEnvironment getEnvironment() {
        return null;
    }

    @Override
    public Collection<PlayingAnimation> getPlayingAnimations() {
        return null;
    }
}
