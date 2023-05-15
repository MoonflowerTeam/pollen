package gg.moonflower.pollen.api.render.animation.v1.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pinwheel.api.geometry.GeometryModel;
import gg.moonflower.pinwheel.api.texture.TextureTable;
import gg.moonflower.pollen.api.animation.v1.controller.PollenAnimationController;
import gg.moonflower.pollen.api.animation.v1.entity.AnimatedEntity;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryBufferSource;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryModelManager;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryTextureManager;
import gg.moonflower.pollen.api.render.geometry.v1.MinecraftGeometryRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * A basic implementation of {@link EntityModel} for {@link GeometryModel}.
 *
 * @param <T> The type of entity this model is rendering
 * @author Ocelot
 * @since 2.0.0
 */
public class AnimatedGeometryEntityModel<T extends Entity & AnimatedEntity> extends EntityModel<T> {

    private final ResourceLocation model;
    private ResourceLocation texture;

    public AnimatedGeometryEntityModel(ResourceLocation model) {
        this.model = model;
        this.texture = null;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float animationTicks, float netHeadYaw, float headPitch) {
        GeometryModel model = this.getModel();
        model.resetTransformation();

        if (!(entity.getAnimationController() instanceof PollenAnimationController controller)) {
            throw new IllegalStateException("Animation controller must implement PollenAnimationController to use AnimatedGeometryEntityModel");
        }

        if (controller.getPlayingAnimations().isEmpty()) {
            return;
        }

        ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
        profiler.push("applyMolangAnimation");
        controller.setLifetime(animationTicks / 20F);
        controller.setRenderParameters(headPitch, netHeadYaw, limbSwing, limbSwingAmount);
        model.applyAnimations(controller);
        profiler.pop();
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.renderToBuffer(matrixStack, Minecraft.getInstance().renderBuffers().bufferSource(), packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void renderToBuffer(PoseStack matrixStack, MultiBufferSource source, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        matrixStack.pushPose();
        matrixStack.translate(0, 1.501F, 0); // LivingEntityRenderer translates after scaling, so we have to undo that
        MinecraftGeometryRenderer.getInstance().render(this.getModel(), this.getTexture(), GeometryBufferSource.entity(source), matrixStack, packedLight, packedOverlay, red, green, blue, alpha);
        matrixStack.popPose();
    }

    /**
     * @return The model this model is wrapping
     */
    public GeometryModel getModel() {
        return GeometryModelManager.getModel(this.model);
    }

    /**
     * @return The name of the texture table to render with
     */

    public TextureTable getTexture() {
        return this.texture != null ? GeometryTextureManager.getTextures(this.texture) : TextureTable.EMPTY;
    }

    /**
     * Sets the texture table to render with.
     *
     * @param texture The new texture
     */
    public void setTexture(@Nullable ResourceLocation texture) {
        this.texture = texture;
    }
}
