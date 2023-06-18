package gg.moonflower.pollen.impl.animation.controller;

import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pollen.api.animation.v1.RenderAnimationTimer;
import gg.moonflower.pollen.api.animation.v1.state.AnimationState;
import gg.moonflower.pollen.api.render.animation.v1.AnimationManager;
import gg.moonflower.pollen.impl.animation.PollenPlayingAnimationImpl;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientStateAnimationControllerImpl extends StateAnimationControllerImpl {

    private final Map<ResourceLocation, PlayingAnimation> playingAnimations;
    private final Map<ResourceLocation, RenderAnimationTimer> animationTimers;

    public ClientStateAnimationControllerImpl(AnimationState[] states, MolangRuntime runtime) {
        super(states, runtime);
        this.playingAnimations = new Object2ObjectArrayMap<>();
        this.animationTimers = new Object2ObjectArrayMap<>();
    }

    private void startAnimation(ResourceLocation name) {
        PlayingAnimation playingAnimation = PlayingAnimation.of(AnimationManager.getAnimation(name));
        if (playingAnimation instanceof PollenPlayingAnimationImpl impl) {
            RenderAnimationTimer timer = this.animationTimers.get(name);
            if (timer != null) {
                impl.setTimer(timer);
            }
        }
        this.playingAnimations.put(name, playingAnimation);
    }

    @Override
    public void clearAnimations() {
        super.clearAnimations();
        this.playingAnimations.clear();
    }

    @Override
    public boolean startAnimations(AnimationState animation) {
        if (super.startAnimations(animation)) {
            for (ResourceLocation name : animation.animations()) {
                this.startAnimation(name);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean stopAnimations(AnimationState animation) {
        if (super.stopAnimations(animation)) {
            for (ResourceLocation name : animation.animations()) {
                this.playingAnimations.remove(name);
            }
            return true;
        }
        return false;
    }

    @Override
    public Collection<PlayingAnimation> getPlayingAnimations() {
        return this.playingAnimations.values();
    }

    @Override
    public void readFromNetwork(FriendlyByteBuf buf) {
        super.readFromNetwork(buf);

        Set<ResourceLocation> removedAnimations = new HashSet<>(this.playingAnimations.keySet());
        for (int id : this.playingStates) {
            for (ResourceLocation name : this.states[id].animations()) {
                if (!removedAnimations.remove(name)) {
                    this.startAnimation(name);
                }
            }
        }

        this.playingAnimations.keySet().removeAll(removedAnimations);
        this.dirty = false;
    }

    @Override
    public void setRenderTimer(ResourceLocation animation, @Nullable RenderAnimationTimer timer) {
        if (timer != null && !RenderAnimationTimer.LINEAR.equals(timer)) {
            this.animationTimers.put(animation, timer);
        } else {
            this.animationTimers.remove(animation);
        }
    }
}
