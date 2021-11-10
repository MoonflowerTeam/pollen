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

/**
 * <p>Defines an entity as having animations states for animating {@link AnimatedModel}.</p>
 * <p>Structure based on <a href=https://github.com/team-abnormals/abnormals-core/blob/main/src/main/java/com/minecraftabnormals/abnormals_core/core/endimator/entity/IEndimatedEntity.java>IEndimatedEntity</a></p>
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
        Level level = entity.level;
        if (level.isClientSide())
            return;
        AnimationState before = entity.getAnimationState();
        entity.setAnimationState(animationState);
        if (before != animationState)
            PollenMessages.PLAY.sendToTracking(entity, new ClientboundSyncAnimationPacket(entity));
    }

    /**
     * Increments the animation tick and automatically handles starting and stopping animations.
     */
    default void animationTick() {
        if (this.isNoAnimationPlaying())
            return;

        AnimationState animationState = this.getAnimationState();
        int animationTick = this.getAnimationTick();
        if (animationTick == 0)
            this.onAnimationStart(animationState);

        this.setAnimationTick(animationTick + 1);
        if (animationTick >= animationState.getTickDuration() - 1) {
            this.onAnimationStop(animationState);
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
        this.setAnimationState(AnimationState.EMPTY);
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
        if (!(this instanceof Entity))
            return;
        Entity entity = (Entity) this;
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
     * Sets the current tick of animation.
     *
     * @param tick The new animation tick
     */
    void setAnimationTick(int tick);

    /**
     * @return The current state of animation
     */
    AnimationState getAnimationState();

    /**
     * Sets the state of animation and resets the animation ticks.
     *
     * @param state The new animation state
     */
    void setAnimationState(AnimationState state);

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
