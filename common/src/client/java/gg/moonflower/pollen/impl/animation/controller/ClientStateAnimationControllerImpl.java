package gg.moonflower.pollen.impl.animation.controller;

import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pollen.api.animation.v1.state.AnimationState;
import gg.moonflower.pollen.api.render.animation.v1.AnimationManager;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ClientStateAnimationControllerImpl extends StateAnimationControllerImpl {

    private final Map<ResourceLocation, PlayingAnimation> playingAnimations;

    public ClientStateAnimationControllerImpl(AnimationState[] states, MolangRuntime.Builder builder) {
        super(states, builder);
        this.playingAnimations = new HashMap<>();
    }

    private void startAnimation(ResourceLocation name) {
        this.playingAnimations.put(name, PlayingAnimation.of(AnimationManager.getAnimation(name)));
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
}
