package gg.moonflower.pollen.pinwheel.api.client.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * <p>Manages the caching of model fields and renders {@link GeometryModel}.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class GeometryModelRenderer {

    private static final Map<Model, Map<String, ModelPart>> MODEL_PARTS = new HashMap<>();
    private static final Map<String, String> MAPPED_NAMES = new HashMap<>();

    private GeometryModelRenderer() {
    }

    /**
     * Copies angles from the parent model to the geometry model.
     *
     * @param parent The parent to copy angles from
     * @param model  The model to apply the angles to
     */
    public static void copyModelAngles(@Nullable Model parent, GeometryModel model) {
        if (parent == null) {
            model.resetTransformation();
            return;
        }
        Map<String, ModelPart> parentParts = MODEL_PARTS.computeIfAbsent(parent, GeometryModelRenderer::mapRenderers);
        for (String modelKey : model.getParentModelKeys()) {
            String name = MAPPED_NAMES.computeIfAbsent(modelKey, key -> VanillaModelMapping.get(parent.getClass(), key));
            if (parentParts.containsKey(name))
                model.copyAngles("parent." + modelKey, parentParts.get(name));
        }
    }

    /**
     * Renders the specified model on the specified parent model.
     *
     * @param model           The model to render
     * @param textureLocation The textures to apply to the model or <code>null</code> to use a missing texture
     * @param buffer          The buffer to draw into
     * @param matrixStack     The current stack of transformations
     * @param packedLight     The packed uv into the light texture the parts should be rendered at
     * @param packedOverlay   The packed uv into the overlay texture the parts should be rendered at
     * @param red             The red factor for color
     * @param green           The green factor for color
     * @param blue            The blue factor for color
     * @param alpha           The alpha factor for color
     */
    public static void render(GeometryModel model, @Nullable ResourceLocation textureLocation, MultiBufferSource buffer, PoseStack matrixStack, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        render(model, textureLocation, buffer, matrixStack, packedLight, packedOverlay, red, green, blue, alpha, null);
    }

    /**
     * Renders the specified model on the specified parent model with custom render type properties.
     *
     * @param model              The model to render
     * @param textureLocation    The textures to apply to the model or <code>null</code> to use a missing texture
     * @param buffer             The buffer to draw into
     * @param matrixStack        The current stack of transformations
     * @param packedLight        The packed uv into the light texture the parts should be rendered at
     * @param packedOverlay      The packed uv into the overlay texture the parts should be rendered at
     * @param red                The red factor for color
     * @param green              The green factor for color
     * @param blue               The blue factor for color
     * @param alpha              The alpha factor for color
     * @param renderTypeConsumer Additional properties to apply to the render type
     */
    public static void render(GeometryModel model, @Nullable ResourceLocation textureLocation, MultiBufferSource buffer, PoseStack matrixStack, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, @Nullable Consumer<RenderType.CompositeState.CompositeStateBuilder> renderTypeConsumer) {
        if (GeometryTextureManager.isReloading())
            return;
        GeometryModelTextureTable textures = textureLocation == null ? GeometryModelTextureTable.EMPTY : GeometryTextureManager.getTextures(textureLocation);
        for (String material : model.getMaterialKeys()) {
            GeometryModelTexture[] layers = textures.getLayerTextures(material);
            for (GeometryModelTexture texture : layers) {
                model.render(material, texture, matrixStack, model.getBuffer(buffer, GeometryTextureManager.getAtlas(), texture, renderTypeConsumer), texture.isGlowing() ? 15728880 : packedLight, packedOverlay, red * texture.getRed(), green * texture.getGreen(), blue * texture.getBlue(), alpha);
            }
        }
    }

    private static Map<String, ModelPart> mapRenderers(Model model) {
        Map<String, ModelPart> renderers = new HashMap<>();
        Class<?> i = model.getClass();
        while (i != null && i != Object.class) {
            for (Field field : i.getDeclaredFields()) {
                if (!field.isSynthetic()) {
                    if (ModelPart.class.isAssignableFrom(field.getType())) {
                        try {
                            field.setAccessible(true);
                            renderers.put(field.getName(), (ModelPart) field.get(model));
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            i = i.getSuperclass();
        }
        return renderers;
    }
}
