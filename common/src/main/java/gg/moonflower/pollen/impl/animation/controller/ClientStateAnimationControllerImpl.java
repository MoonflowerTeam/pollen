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
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClientStateAnimationControllerImpl extends StateAnimationControllerImpl {

    private final Map<ResourceLocation, PlayingAnimation> playingAnimations;
    private final Map<ResourceLocation, TransitionTimer> transitionAnimations;
    private final Map<ResourceLocation, RenderAnimationTimer> animationTimers;

    public ClientStateAnimationControllerImpl(AnimationState[] states, MolangRuntime runtime) {
        super(states, runtime);
        this.playingAnimations = new Object2ObjectArrayMap<>();
        this.transitionAnimations = new Object2ObjectArrayMap<>();
        this.animationTimers = new Object2ObjectArrayMap<>();
    }

    private void startAnimation(ResourceLocation name) {
        PollenPlayingAnimationImpl playingAnimation = new PollenPlayingAnimationImpl(AnimationManager.getAnimation(name));
        RenderAnimationTimer timer = this.animationTimers.get(name);
        if (timer != null) {
            playingAnimation.setTimer(timer);
        }
        this.playingAnimations.put(name, playingAnimation);
        this.transitionAnimations.remove(name);
    }

    @Override
    public void updateRenderTime(float partialTicks) {
        super.updateRenderTime(partialTicks);

        Iterator<Map.Entry<ResourceLocation, TransitionTimer>> iterator = this.transitionAnimations.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ResourceLocation, TransitionTimer> entry = iterator.next();
            TransitionTimer timer = entry.getValue();
            ResourceLocation name = entry.getKey();

            PlayingAnimation animation = this.playingAnimations.get(name);
            if (animation == null) {
                iterator.remove();
                continue;
            }

            float time = Mth.clamp((timer.value + partialTicks) / (float) timer.max, 0.0F, 1.0F);
            animation.setWeight(timer.transitionOut ? 1.0F - time : time);
        }
    }

    @Override
    public void tick() {
        super.tick();

        Iterator<Map.Entry<ResourceLocation, TransitionTimer>> iterator = this.transitionAnimations.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ResourceLocation, TransitionTimer> entry = iterator.next();
            TransitionTimer timer = entry.getValue();
            ResourceLocation name = entry.getKey();

            if (timer.tick()) {
                iterator.remove();
                if (timer.transitionOut) {
                    this.playingAnimations.remove(name);
                }
            }
        }
    }

    @Override
    public void clearAnimations(int transitionTicks) {
        super.clearAnimations(transitionTicks);
        if (transitionTicks > 0) {
            for (ResourceLocation name : this.playingAnimations.keySet()) {
                this.transitionAnimations.put(name, new TransitionTimer(transitionTicks, true));
            }
        } else {
            this.playingAnimations.clear();
        }
    }

    @Override
    public boolean startAnimations(AnimationState animation, int transitionTicks) {
        if (super.startAnimations(animation, transitionTicks)) {
            for (ResourceLocation name : animation.animations()) {
                this.startAnimation(name);
                if (transitionTicks > 0) {
                    this.transitionAnimations.put(name, new TransitionTimer(transitionTicks, false));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean stopAnimations(AnimationState animation, int transitionTicks) {
        if (super.stopAnimations(animation, transitionTicks)) {
            for (ResourceLocation name : animation.animations()) {
                if (transitionTicks > 0) {
                    this.transitionAnimations.put(name, new TransitionTimer(transitionTicks, true));
                } else {
                    this.playingAnimations.remove(name);
                }
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
            int transitionTicks = this.stateTransitions[id];
            for (ResourceLocation name : this.states[id].animations()) {
                // If the animation was not already playing, then start playing it
                if (!removedAnimations.remove(name)) {
                    this.startAnimation(name);
                    if (transitionTicks > 0) {
                        this.transitionAnimations.put(name, new TransitionTimer(transitionTicks, false));
                    }
                }
            }
        }

        for (ResourceLocation removedAnimation : removedAnimations) {
            this.transitionAnimations.put(removedAnimation, new TransitionTimer(8, true));
        }

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

    @Override
    public RenderAnimationTimer getRenderTimer(ResourceLocation animation) {
        return this.animationTimers.getOrDefault(animation, RenderAnimationTimer.LINEAR);
    }

    private static class TransitionTimer {

        private final int max;
        private final boolean transitionOut;
        private int value;

        public TransitionTimer(int value, boolean transitionOut) {
            this.max = value;
            this.transitionOut = transitionOut;
            this.value = 0;
        }

        public boolean tick() {
            return this.value++ >= this.max;
        }
    }
}
