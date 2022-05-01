package gg.moonflower.pollen.pinwheel.api.client.animation;

import com.google.common.collect.Streams;
import gg.moonflower.pollen.pinwheel.api.common.animation.AnimatedEntity;
import gg.moonflower.pollen.pinwheel.api.common.animation.AnimationEffectHandler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Implementation of {@link GeometryEntityRenderer} for {@link AnimatedEntity}.
 *
 * @param <T> The type of entity to render
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class AnimatedEntityRenderer<T extends Mob & AnimatedEntity> extends GeometryEntityRenderer<T> {

    public AnimatedEntityRenderer(EntityRendererProvider.Context context, ResourceLocation model, float shadowSize) {
        super(context, model, shadowSize);
    }

    @Nullable
    @Override
    protected AnimationEffectHandler getEffectHandler(T entity) {
        return entity.getAnimationEffects();
    }

    @Override
    protected float getBob(T entity, float partialTicks) {
        return (entity.isNoAnimationPlaying() ? entity.tickCount + partialTicks : entity.getRenderAnimationTick(partialTicks));
    }

    @Override
    public ResourceLocation[] getAnimations(T entity) {
        ResourceLocation[] animations = entity.isNoAnimationPlaying() ? entity.getIdleAnimationState().getAnimations() : entity.getAnimationState().getAnimations();
        if (entity.isAnimationTransitioning())
            return Streams.concat(Arrays.stream(animations), Arrays.stream(entity.getTransitionAnimationState().getAnimations())).toArray(ResourceLocation[]::new);
        return animations;
    }

    @Override
    public float[] getAnimationWeights(T entity) {
        return new float[0];
    }

    @Override
    public float[] getAnimationWeights(T entity, float partialTicks) {
        if (entity.isAnimationTransitioning()) {
            ResourceLocation[] animations = this.getAnimations(entity);
            float[] weights = new float[animations.length];
            int transitionAnimationStart = animations.length - entity.getTransitionAnimationState().getAnimations().length;
            float transition = entity.getRenderAnimationTransitionTick(partialTicks) / (float) entity.getAnimationTransitionLength();
            for (int i = 0; i < weights.length; i++)
                weights[i] = i < transitionAnimationStart ? (1.0F - transition) : transition;
            return weights;
        }
        return new float[0];
    }
}
