package gg.moonflower.pollen.api.render.geometry.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pinwheel.api.geometry.GeometryModel;
import gg.moonflower.pinwheel.api.geometry.GeometryRenderer;
import gg.moonflower.pinwheel.api.texture.TextureTable;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import gg.moonflower.pollen.api.pinwheelbridge.v1.PinwheelBridge;
import gg.moonflower.pollen.impl.render.geometry.MinecraftGeometryRendererImpl;

/**
 * Draws {@link GeometryModel} using the Minecraft rendering pipeline.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface MinecraftGeometryRenderer extends GeometryRenderer {

    /**
     * Draws the specified geometry model into the specified buffer.
     *
     * @param model         The model to render
     * @param textureTable  The texture table to use
     * @param bufferSource  The source of builders
     * @param poseStack     The stack of matrix transformations
     * @param packedLight   The packed light UV coordinates
     * @param packedOverlay The packed overlay UV coordinates
     * @param red           The red tint to apply
     * @param green         The green tint to apply
     * @param blue          The blue tint to apply
     * @param alpha         The alpha tint to apply
     */
    default void render(GeometryModel model, TextureTable textureTable, GeometryBufferSource bufferSource, PoseStack poseStack, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.render(model, textureTable, bufferSource, PinwheelBridge.wrap(poseStack), packedLight, packedOverlay, red, green, blue, alpha);
    }

    /**
     * Draws the specified geometry model into the specified buffer.
     *
     * @param model         The model to render
     * @param textureTable  The texture table to use
     * @param bufferSource  The source of builders
     * @param matrixStack   The stack of matrix transformations
     * @param packedLight   The packed light UV coordinates
     * @param packedOverlay The packed overlay UV coordinates
     * @param red           The red tint to apply
     * @param green         The green tint to apply
     * @param blue          The blue tint to apply
     * @param alpha         The alpha tint to apply
     */
    void render(GeometryModel model, TextureTable textureTable, GeometryBufferSource bufferSource, MatrixStack matrixStack, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);

    /**
     * @return The instance of the geometrry renderer
     */
    static MinecraftGeometryRenderer getInstance() {
        return MinecraftGeometryRendererImpl.INSTANCE;
    }
}
