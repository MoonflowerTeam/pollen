package gg.moonflower.pollen.impl.animation.controller;

import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
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
        this.idleAnimations = new ResourceLocation[0];
    }

    private void tickIdle() {
        if (!this.playingAnimations.isEmpty()) {
            return;
        }

        for (ResourceLocation animation : this.idleAnimations) {
            PollenPlayingAnimationImpl playingAnimation = new PollenPlayingAnimationImpl(AnimationManager.getAnimation(animation));
            playingAnimation.setTimer(this.delegate.getRenderTimer(animation));
            this.playingAnimations.add(playingAnimation);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.delegate.isNoAnimationPlaying()) {
            this.playingAnimations.clear();
            return;
        }

        for (PlayingAnimation playingAnimation : this.playingAnimations) {
            if (playingAnimation instanceof PollenPlayingAnimationImpl impl) {
                impl.tick();
            }
        }

        if (this.idleAnimations.length > 0) {
            this.tickIdle();
        }
    }

    @Override
    public void updateRenderTime(float partialTicks) {
        super.updateRenderTime(partialTicks);
        if (this.delegate.isNoAnimationPlaying()) {
            for (PlayingAnimation animation : this.playingAnimations) {
                if (animation instanceof PollenPlayingAnimationImpl impl) {
                    impl.setRenderTime(partialTicks);
                }
            }
        }
    }

    @Override
    public Collection<PlayingAnimation> getPlayingAnimations() {
        return this.delegate.isNoAnimationPlaying() ? this.playingAnimations : super.getPlayingAnimations();
    }

    @Override
    public ResourceLocation[] getIdleAnimations() {
        return this.idleAnimations;
    }

    @Override
    public void setIdleAnimations(ResourceLocation... animations) {
        if (Arrays.equals(this.idleAnimations, animations)) {
            return;
        }

        this.idleAnimations = animations;
        if (this.delegate.isNoAnimationPlaying()) {
            this.playingAnimations.clear();
            this.tickIdle();
        }
    }
}
