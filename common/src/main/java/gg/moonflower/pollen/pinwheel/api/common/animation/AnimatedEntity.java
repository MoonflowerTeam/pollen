package gg.moonflower.pollen.pinwheel.api.common.animation;

import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.core.network.play.ClientboundSyncAnimationPacket;
import gg.moonflower.pollen.pinwheel.api.client.animation.AnimatedModel;
import gg.moonflower.pollen.pinwheel.core.client.animation.AnimationEffectSound;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

// TODO in 2.0.0 Redesign this to be more modular. Currently, in order to extend something else, you have to copy the base implementation of this into the entity which is pretty bad

/**
 * <p>Defines an entity as having animations states for animating {@link AnimatedModel}.</p>
 * <p>Structure based on <a href="https://github.com/team-abnormals/blueprint/blob/1.18.x/src/main/java/com/teamabnormals/blueprint/core/endimator/Endimatable.java">Endimatable</a></p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface AnimatedEntity extends AnimationEffectSource {

    /**
     * Sets the animation for the specified entity on the server side, and syncs with clients.
     *
     * @param entity         The entity to sync the animation of
     * @param animationState The new animation state
     * @param <T>            The type of entity to set the animation state for
     */
    static <T extends Entity & AnimatedEntity> void setAnimation(T entity, AnimationState animationState) {
        setAnimation(entity, animationState, 0);
    }

    /**
     * Sets the animation for the specified entity on the server side, and syncs with clients.
     *
     * @param entity         The entity to sync the animation of
     * @param animationState The new animation state
     * @param duration       The amount of time to transition to the next animation
     * @param <T>            The type of entity to set the animation state for
     */
    static <T extends Entity & AnimatedEntity> void setAnimation(T entity, AnimationState animationState, int duration) {
        Level level = entity.level;
        if (level.isClientSide())
            return;
        AnimationState before = entity.getAnimationState();
        entity.setAnimationState(animationState, duration);
        if (before != animationState)
            PollenMessages.PLAY.sendToTracking(entity, new ClientboundSyncAnimationPacket(entity, duration));
    }

    /**
     * Increments the animation tick and automatically handles starting and stopping animations.
     */
    default void animationTick() {
        if (!this.isAnimationTransitioning() && this.isNoAnimationPlaying())
            return;

        AnimationState animationState = this.getAnimationState();
        int animationTick = this.getAnimationTick();
        if (animationTick == 0)
            this.onAnimationStart(animationState);

        this.setAnimationTick(animationTick + 1);
        int animationTransitionTick = this.getAnimationTransitionTick();
        if (this.isAnimationTransitioning()) {
            this.setAnimationTransitionTick(animationTransitionTick + 1);
            if (!this.isAnimationTransitioning()) {
                this.setAnimationState(this.getTransitionAnimationState(), 0);
            }
        } else if (animationTick >= animationState.getTickDuration() - 1) { // only stop animation if not transitioning
            this.resetAnimationState();
        }
    }

    /**
     * Called when a new animation has just started playing.
     *
     * @param state The animation state playing
     */
    default void onAnimationStart(AnimationState state) {
        AnimationEffectHandler effectHandler = this.getAnimationEffects();
        if (effectHandler != null)
            effectHandler.reset();
    }

    /**
     * Called just before an animation state completes.
     *
     * @param state The animation state about to finish
     */
    default void onAnimationStop(AnimationState state) {
    }

    /**
     * Called to reset the animation state back to the default. By default, this will set the state to {@link AnimationState#EMPTY}.
     */
    default void resetAnimationState() {
        this.resetAnimationState(0);
    }

    /**
     * Called to reset the animation state back to the default. By default, this will set the state to {@link AnimationState#EMPTY}.
     *
     * @param duration The amount of time to transition to the new state over
     */
    default void resetAnimationState(int duration) {
        this.setAnimationState(AnimationState.EMPTY, duration);
    }

    /**
     * Called when a sound event should be played.
     *
     * @param animation   The animation the effect is playing for
     * @param soundEffect The effect to play
     */
    @Environment(EnvType.CLIENT)
    @Override
    default void handleSoundEffect(AnimationData animation, AnimationData.SoundEffect soundEffect) {
        if (!(this instanceof Entity))
            return;

        Entity entity = (Entity) this;
        ResourceLocation sound = ResourceLocation.tryParse(soundEffect.getEffect());
        if (sound != null) {
            MolangRuntime runtime = MolangRuntime.runtime().create(1.0F); // 1.0 is the default pitch and volume
            Minecraft.getInstance().getSoundManager().play(new AnimationEffectSound(sound, entity.getSoundSource(), animation, entity, soundEffect.getPitch().safeResolve(runtime), soundEffect.getVolume().safeResolve(runtime), soundEffect.isLoop()));
        }
    }

    /**
     * Called when a particle effect should be play.
     *
     * @param animation      The animation the effect is playing for
     * @param particleEffect The particle information to play
     * @param xOffset        The x offset of the effect from the origin of the model
     * @param yOffset        The y offset of the effect from the origin of the model
     * @param zOffset        The z offset of the effect from the origin of the model
     */
    @Environment(EnvType.CLIENT)
    @Override
    default void handleParticleEffect(AnimationData animation, AnimationData.ParticleEffect particleEffect, double xOffset, double yOffset, double zOffset) {
        // TODO implement
    }

    /**
     * Called when a custom effect is placed on the timeline.
     *
     * @param animation      The animation the effect is playing for
     * @param timelineEffect The effect on the timeline
     */
    @Environment(EnvType.CLIENT)
    @Override
    default void handleTimelineEffect(AnimationData animation, AnimationData.TimelineEffect timelineEffect) {
    }

    /**
     * @return The current tick of animation
     */
    int getAnimationTick();

    /**
     * @return The current tick of animation transition
     */
    int getAnimationTransitionTick();

    /**
     * @return The length of animation transition
     */
    int getAnimationTransitionLength();

    /**
     * Calculates the current animation tick time.
     *
     * @param partialTicks The percentage from last tick to this tick
     * @return The interpolated tick
     */
    default float getRenderAnimationTick(float partialTicks) {
        return this.getAnimationTick() + partialTicks;
    }

    /**
     * Calculates the current animation transition tick time.
     *
     * @param partialTicks The percentage from last tick to this tick
     * @return The interpolated tick
     */
    default float getRenderAnimationTransitionTick(float partialTicks) {
        return this.getAnimationTransitionTick() + partialTicks;
    }

    /**
     * Sets the current tick of animation.
     *
     * @param tick The new animation tick
     */
    void setAnimationTick(int tick);

    /**
     * Sets the current tick of animation transition.
     *
     * @param transitionTick The new animation transition tick
     */
    void setAnimationTransitionTick(int transitionTick);

    /**
     * Sets the max tick of animation transition.
     *
     * @param transitionLength The new animation transition length to tick to
     */
    void setAnimationTransitionLength(int transitionLength);

    /**
     * @return The current state of animation
     */
    AnimationState getAnimationState();

    /**
     * @return The next state of animation
     */
    AnimationState getTransitionAnimationState();

    /**
     * @return The animations to use when no other animations are playing
     */
    default AnimationState getIdleAnimationState() {
        return AnimationState.EMPTY;
    }

    /**
     * Sets the state of animation and resets the animation ticks.
     *
     * @param state The new animation state
     */
    void setAnimationState(AnimationState state);

    /**
     * Sets the state of animation and resets the animation ticks.
     *
     * @param state The new animation state
     */
    void setTransitionAnimationState(AnimationState state);

    /**
     * Sets the state of animation and resets the animation ticks.
     *
     * @param state    The new animation state
     * @param duration The amount of ticks to transition to the new state
     */
    default void setAnimationState(AnimationState state, int duration) {
        this.setTransitionAnimationState(state);
        if (duration <= 0) {
            this.setAnimationState(state);
        } else {
            this.setAnimationTransitionLength(duration);
        }
        this.setAnimationTransitionTick(0);
    }

    /**
     * @return The handler for animation effects on this entity or <code>null</code> to ignore effects
     */
    @Nullable
    AnimationEffectHandler getAnimationEffects();

    /**
     * @return Whether no animation is currently playing
     */
    default boolean isNoAnimationPlaying() {
        return this.getAnimationState() == AnimationState.EMPTY;
    }

    /**
     * @return Whether the current animation is transitioning to a new animation
     */
    default boolean isAnimationTransitioning() {
        return this.getAnimationTransitionTick() < this.getAnimationTransitionLength();
    }

    /**
     * Checks to see if the specified animation is playing.
     *
     * @param state The animation state to check
     * @return Whether that state is playing
     */
    default boolean isAnimationPlaying(AnimationState state) {
        return this.getAnimationState() == state;
    }

    /**
     * @return All animation states. This is used for syncing the current state with clients
     */
    AnimationState[] getAnimationStates();
}
