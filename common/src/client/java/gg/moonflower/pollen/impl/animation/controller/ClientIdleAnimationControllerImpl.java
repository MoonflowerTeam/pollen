package gg.moonflower.pollen.impl.animation.controller;

import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pollen.api.animation.v1.controller.DelegateAnimationController;
import gg.moonflower.pollen.api.animation.v1.controller.IdleAnimationController;
import gg.moonflower.pollen.api.animation.v1.controller.PollenAnimationController;
import gg.moonflower.pollen.api.render.animation.v1.AnimationManager;
import gg.moonflower.pollen.impl.animation.PollenPlayingAnimationImpl;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ApiStatus.Internal
public class ClientIdleAnimationControllerImpl extends IdleAnimationControllerImpl {

    private final List<PlayingAnimation> playingAnimations;

    private ResourceLocation[] idleAnimations;

    public ClientIdleAnimationControllerImpl(PollenAnimationController delegate) {
        super(delegate);
        this.playingAnimations = new ArrayList<>();
    }

    private void startIdle() {
        if (this.playingAnimations.isEmpty()) {
            return;
        }

        for (ResourceLocation animation : this.idleAnimations) {
            PlayingAnimation playingAnimation = PlayingAnimation.of(AnimationManager.getAnimation(animation));
            if (playingAnimation instanceof PollenPlayingAnimationImpl impl) {
                impl.setTimer(this.delegate.getRenderTimer(animation));
            }
            this.playingAnimations.add(playingAnimation);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.delegate.isNoAnimationPlaying()) {
            for (PlayingAnimation playingAnimation : this.playingAnimations) {
                if (playingAnimation instanceof PollenPlayingAnimationImpl impl) {
                    impl.tick();
                }
            }

            this.startIdle();
        } else {
            this.playingAnimations.clear();
        }
    }

    @Override
    public Collection<PlayingAnimation> getPlayingAnimations() {
        return !this.playingAnimations.isEmpty() ? this.playingAnimations : super.getPlayingAnimations();
    }

    @Override
    public ResourceLocation[] getIdleAnimations() {
        return this.idleAnimations;
    }

    @Override
    public void setIdleAnimations(ResourceLocation... animations) {
        if (!Arrays.equals(this.idleAnimations, animations)) {
            return;
        }

        this.idleAnimations = animations;
        if (this.delegate.isNoAnimationPlaying()) {
            this.playingAnimations.clear();
            this.startIdle();
        }
    }
}
