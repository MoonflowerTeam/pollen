package gg.moonflower.pollen.pinwheel.api.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import gg.moonflower.pollen.pinwheel.api.common.animation.AnimationEffectHandler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

/**
 * Renders any entity using an {@link AnimatedGeometryEntityModel}.
 *
 * @param <T> The type of entity to render
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class GeometryEntityRenderer<T extends Mob> extends MobRenderer<T, AnimatedGeometryEntityModel<T>> {

    public GeometryEntityRenderer(EntityRendererProvider.Context context, ResourceLocation model, float shadowSize) {
        super(context, new AnimatedGeometryEntityModel<>(model), shadowSize);
    }

    @Override
    protected abstract float getBob(T entity, float partialTicks);

    @Nullable
    protected AnimationEffectHandler getEffectHandler(T entity) {
        return null;
    }

    @Override
    protected void setupRotations(T entity, PoseStack matrixStack, float ticksExisted, float rotY, float partialTicks) {
        super.setupRotations(entity, matrixStack, ticksExisted, rotY, partialTicks);
        this.model.setTexture(this.getTextureTableLocation(entity, partialTicks));
        this.model.setAnimations(this.getAnimations(entity, partialTicks));
        this.model.setAnimationWeights(this.getAnimationWeights(entity, partialTicks));
        AnimationEffectHandler effectHandler = this.getEffectHandler(entity);
        if (effectHandler != null)
            effectHandler.tick(this.getAnimations(entity), this.getBob(entity, partialTicks) / 20.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return GeometryTextureManager.getAtlas().getAtlasLocation();
    }

    /**
     * Fetches the animations to play.
     *
     * @param entity       The entity to get the animations for
     * @param partialTicks The percentage from last tick to this tick
     * @return The animations to play
     */
    public ResourceLocation[] getAnimations(T entity, float partialTicks) {
        return this.getAnimations(entity);
    }

    /**
     * Fetches the animation weights to use.
     *
     * @param entity       The entity to get the animations for
     * @param partialTicks The percentage from last tick to this tick
     * @return The weights to add on top
     */
    public float[] getAnimationWeights(T entity, float partialTicks) {
        return this.getAnimationWeights(entity);
    }

    /**
     * Fetches the texture table location to use for the specified entity.
     *
     * @param entity       The entity to get the texture for
     * @param partialTicks The percentage from last tick to this tick
     * @return The location of the texture table for that entity
     */
    public ResourceLocation getTextureTableLocation(T entity, float partialTicks) {
        return this.getTextureTableLocation(entity);
    }

    /**
     * Fetches the animations to play.
     *
     * @param entity The entity to get the animations for
     * @return The animations to play
     * @deprecated Use {@link #getAnimations(Mob, float)} instead
     */
    @Deprecated
    public abstract ResourceLocation[] getAnimations(T entity);

    /**
     * Fetches the animation weights to use.
     *
     * @param entity The entity to get the animations for
     * @return The weights to add on top
     * @deprecated Use {@link #getAnimationWeights(Mob, float)} instead
     */
    @Deprecated
    public abstract float[] getAnimationWeights(T entity);

    /**
     * Fetches the texture table location to use for the specified entity.
     *
     * @param entity The entity to get the texture for
     * @return The location of the texture table for that entity
     * @deprecated Use {@link #getTextureTableLocation(Mob, float)} instead
     */
    @Deprecated
    public abstract ResourceLocation getTextureTableLocation(T entity);
}
