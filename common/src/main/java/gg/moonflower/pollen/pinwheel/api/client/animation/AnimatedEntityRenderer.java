package gg.moonflower.pollen.pinwheel.api.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import gg.moonflower.pollen.pinwheel.api.common.animation.AnimatedEntity;
import gg.moonflower.pollen.pinwheel.api.common.animation.AnimationEffectHandler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;

/**
 * <p>Renders an {@link AnimatedEntity} using {@link AnimatedGeometryEntityModel}.</p>
 *
 * @param <T> The type of entity to render
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class AnimatedEntityRenderer<T extends PathfinderMob & AnimatedEntity> extends MobRenderer<T, AnimatedGeometryEntityModel<T>> {

    public AnimatedEntityRenderer(EntityRendererProvider.Context context, ResourceLocation model, float shadowSize) {
        super(context, new AnimatedGeometryEntityModel<>(model), shadowSize);
    }

    @Override
    protected float getBob(T entity, float partialTicks) {
        return (entity.isNoAnimationPlaying() ? entity.tickCount + partialTicks : entity.getRenderAnimationTick(partialTicks));
    }

    @Override
    protected void setupRotations(T entity, PoseStack matrixStack, float ticksExisted, float rotY, float partialTicks) {
        super.setupRotations(entity, matrixStack, ticksExisted, rotY, partialTicks);
        this.model.setTexture(this.getTextureTableLocation(entity));
        this.model.setAnimations(this.getAnimations(entity));
        AnimationEffectHandler effectHandler = entity.getAnimationEffects();
        if (effectHandler != null)
            effectHandler.tick(this.getAnimations(entity), this.getBob(entity, partialTicks) / 20.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return GeometryTextureManager.getAtlas().getAtlasLocation();
    }

    /**
     * Fetches the default animations to play.
     *
     * @param entity The entity to get the animations for
     * @return The animations to play
     */
    public ResourceLocation[] getAnimations(T entity) {
        return entity.isNoAnimationPlaying() ? entity.getIdleAnimationState().getAnimations() : entity.getAnimationState().getAnimations();
    }

    /**
     * Fetches the texture table location to use for the specified entity.
     *
     * @param entity The entity to get the texture for
     * @return The location of the texture table for that entity
     */
    public abstract ResourceLocation getTextureTableLocation(T entity);
}
