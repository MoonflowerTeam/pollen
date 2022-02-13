package gg.moonflower.pollen.pinwheel.api.client.animation;

import gg.moonflower.pollen.pinwheel.api.common.animation.AnimatedEntity;
import gg.moonflower.pollen.pinwheel.api.common.animation.AnimationEffectHandler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

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
        return entity.isNoAnimationPlaying() ? entity.getIdleAnimationState().getAnimations() : entity.getAnimationState().getAnimations();
    }
}
