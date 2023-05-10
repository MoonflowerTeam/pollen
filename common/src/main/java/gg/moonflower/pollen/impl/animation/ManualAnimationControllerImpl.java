package gg.moonflower.pollen.impl.animation;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pollen.api.animation.v1.controller.ManualAnimationController;
import gg.moonflower.pollen.api.animation.v1.controller.SerializableAnimationController;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ManualAnimationControllerImpl implements ManualAnimationController, SerializableAnimationController {

    private final AnimationData[] data;
    private final PlayingAnimation[] animations;
    private final Map<AnimationData, Integer> mapping;
    private final IntSet playingAnimations;
    private Set<PlayingAnimation> playingAnimationSet;

    public ManualAnimationControllerImpl(AnimationData[] animations) {
        this.data = animations;
        this.animations = new PlayingAnimation[animations.length];
        this.mapping = IntStream.range(0, animations.length).boxed().collect(Collectors.toMap(i -> animations[i], i -> i));
        this.playingAnimations = new IntOpenHashSet();
        this.playingAnimationSet = null;
    }

    private PlayingAnimation addAnimation(int id) {
        this.playingAnimations.add(id);
        this.playingAnimationSet = null;

        PlayingAnimation playingAnimation = PlayingAnimation.of(this.data[id]);
        this.animations[id] = playingAnimation;
        return playingAnimation;
    }

    private boolean removeAnimation(int id) {
        if (this.animations[id] == null) {
            return false;
        }

        this.playingAnimations.remove(id);
        this.playingAnimationSet = null;

        this.animations[id] = null;
        return true;
    }

    @Override
    public MolangEnvironment getEnvironment() {
        return null;
    }

    @Override
    public Collection<PlayingAnimation> getPlayingAnimations() {
        if (this.playingAnimationSet == null) {
            this.playingAnimationSet = this.playingAnimations.intStream().mapToObj(i -> this.animations[i]).collect(Collectors.toSet());
        }
        return this.playingAnimationSet;
    }

    @Override
    public void clearAnimations() {
        Arrays.fill(this.animations, null);
        this.playingAnimations.clear();
        this.playingAnimationSet = Collections.emptySet();
    }

    @Override
    public PlayingAnimation addAnimation(AnimationData animation) {
        Integer id = this.mapping.get(animation);
        if (id == null) {
            throw new IllegalArgumentException("Invalid animation: " + animation);
        }

        return this.addAnimation(id);
    }

    @Override
    public boolean removeAnimation(AnimationData animation) {
        Integer id = this.mapping.get(animation);
        if (id == null) {
            return false;
        }

        return this.removeAnimation(id);
    }

    @Override
    public @Nullable PlayingAnimation getPlayingAnimation(AnimationData animation) {
        Integer id = this.mapping.get(animation);
        if (id == null) {
            return null;
        }

        return this.animations[id];
    }

    @Override
    public void tick() {
        for (int id : this.playingAnimations) {
            PlayingAnimation animation = this.animations[id];
            animation.setAnimationTime(animation.getAnimationTime() + 0.05F);
            if (animation.isDone()) {
                this.removeAnimation(id);
            }
        }
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeVarIntArray(this.playingAnimations.toIntArray());
        for (int id : this.playingAnimations) {
            PlayingAnimation animation = this.animations[id];
            buf.writeVarInt((int) (animation.getAnimationTime() / 0.05F));
            buf.writeFloat(animation.getWeightFactor());
        }
    }

    @Override
    public void readFromNetwork(FriendlyByteBuf buf) {
        int[] playingAnimations = buf.readVarIntArray();

        IntSet removed = new IntArraySet(this.playingAnimations);
        for (int id : playingAnimations) {
            removed.remove(id);
            PlayingAnimation animation = this.animations[id];
            if (animation == null) {
                animation = this.addAnimation(id);
            }
            animation.setAnimationTime(buf.readVarInt() * 0.05F);
            animation.setWeight(buf.readFloat());
        }

        for (int id : removed) {
            this.animations[id] = null;
        }
        if (!removed.isEmpty()) {
            this.playingAnimationSet = null;
        }
    }
}
