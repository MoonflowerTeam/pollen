package gg.moonflower.pollen.api.render.animation.v1.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.api.animation.v1.entity.AnimatedEntity;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryTextureManager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/**
 * Renders any entity using an {@link AnimatedGeometryEntityModel}.
 *
 * @param <T> The type of entity to render
 * @author Ocelot
 * @since 2.0.0
 */
public abstract class GeometryEntityRenderer<T extends Mob & AnimatedEntity> extends MobRenderer<T, AnimatedGeometryEntityModel<T>> {

    public GeometryEntityRenderer(EntityRendererProvider.Context context, ResourceLocation model, float shadowSize) {
        super(context, new AnimatedGeometryEntityModel<>(model), shadowSize);
    }

    @Override
    protected abstract float getBob(T entity, float partialTicks);

    @Override
    protected void setupRotations(T entity, PoseStack matrixStack, float ticksExisted, float rotY, float partialTicks) {
        super.setupRotations(entity, matrixStack, ticksExisted, rotY, partialTicks);
        this.model.setTexture(this.getTextureTableLocation(entity, partialTicks));
        // TODO animation effects
//        AnimationEffectHandler effectHandler = this.getEffectHandler(entity);
//        if (effectHandler != null)
//            effectHandler.tick(this.getAnimations(entity), this.getBob(entity, partialTicks) / 20.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return GeometryTextureManager.getAtlas().getAtlasLocation();
    }

    /**
     * Fetches the texture table location to use for the specified entity.
     *
     * @param entity       The entity to get the texture for
     * @param partialTicks The percentage from last tick to this tick
     * @return The location of the texture table for that entity
     */
    public abstract ResourceLocation getTextureTableLocation(T entity, float partialTicks);
}
