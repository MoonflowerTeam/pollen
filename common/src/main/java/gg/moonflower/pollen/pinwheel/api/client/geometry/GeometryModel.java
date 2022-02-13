package gg.moonflower.pollen.pinwheel.api.client.geometry;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pollen.core.mixin.client.AgeableListModelAccessor;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryAtlasTexture;
import gg.moonflower.pollen.pinwheel.api.common.geometry.GeometryModelData;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import gg.moonflower.pollen.pinwheel.core.client.geometry.BedrockGeometryModel;
import gg.moonflower.pollen.pinwheel.core.client.geometry.JavaModelConverter;
import gg.moonflower.pollen.pinwheel.core.client.geometry.LocalGeometryModelLoader;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * <p>An abstract part for geometry models that can be queried by {@link LocalGeometryModelLoader}.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface GeometryModel {

    /**
     * A blank model that can be used for empty entries.
     */
    GeometryModel EMPTY = new GeometryModel() {

        @Override
        public void render(String material, GeometryModelTexture texture, PoseStack matrixStack, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        }

        @Override
        public void resetTransformation() {
        }

        @Override
        public void copyAngles(@Nullable String parent, ModelPart modelPart) {
        }

        @Override
        public Optional<ModelPart> getModelPart(String part) {
            return Optional.empty();
        }

        @Override
        public ModelPart[] getChildRenderers(String part) {
            return new ModelPart[0];
        }

        @Override
        public ModelPart[] getModelParts() {
            return new ModelPart[0];
        }

        @Override
        public String[] getParentModelKeys() {
            return new String[0];
        }

        @Override
        public String[] getMaterialKeys() {
            return new String[0];
        }

        @Override
        public float getTextureWidth() {
            return 16;
        }

        @Override
        public float getTextureHeight() {
            return 16;
        }
    };

    /**
     * Creates a new {@link GeometryModel} from the specified model data.
     *
     * @param data The data deserialized from JSON
     * @return A new model from the data
     */
    static GeometryModel create(GeometryModelData data) {
        return new BedrockGeometryModel(data.getDescription().getTextureWidth(), data.getDescription().getTextureHeight(), data.getBones());
    }

    /**
     * Creates a new {@link GeometryModel} from the specified model data.
     *
     * @param textureWidth  The width of the texture in pixels
     * @param textureHeight The height of the texture in pixels
     * @param bones         The bones in the model
     * @return A new model from the data
     */
    static GeometryModel create(int textureWidth, int textureHeight, GeometryModelData.Bone... bones) {
        return new BedrockGeometryModel(textureWidth, textureHeight, bones);
    }

    /**
     * Creates a new {@link GeometryModel} from the specified model data. This should be used for all models that don't extend {@link ListModel} or {@link AgeableListModel}
     *
     * @param textureWidth  The width of the texture in pixels
     * @param textureHeight The height of the texture in pixels
     * @param bones         The parent bones in a Java model. These are expected to be the individual parts that are rendered
     * @return A new model from the data
     */
    static GeometryModel create(int textureWidth, int textureHeight, ModelPart... bones) {
        return new BedrockGeometryModel(textureWidth, textureHeight, JavaModelConverter.convert(bones));
    }

    /**
     * Creates a new {@link GeometryModel} from the specified model data.
     *
     * @param textureWidth  The width of the texture in pixels
     * @param textureHeight The height of the texture in pixels
     * @param model         The model to get the bones from
     * @return A new model from the data
     */
    static GeometryModel create(int textureWidth, int textureHeight, ListModel<?> model) {
        return new BedrockGeometryModel(textureWidth, textureHeight, JavaModelConverter.convert(Iterables.toArray(model.parts(), ModelPart.class)));
    }

    /**
     * Creates a new {@link GeometryModel} from the specified model data.
     *
     * @param textureWidth  The width of the texture in pixels
     * @param textureHeight The height of the texture in pixels
     * @param model         The model to get the bones from
     * @return A new model from the data
     */
    static GeometryModel create(int textureWidth, int textureHeight, AgeableListModel<?> model) {
        AgeableListModelAccessor accessor = (AgeableListModelAccessor) model;
        return new BedrockGeometryModel(textureWidth, textureHeight, JavaModelConverter.convert(Iterables.toArray(Iterables.concat(accessor.invokeHeadParts(), accessor.invokeBodyParts()), ModelPart.class)));
    }

    /**
     * Renders a specific part with the specified texture key.
     *
     * @param material      The name of the material being rendered
     * @param texture       The texture currently being rendered
     * @param matrixStack   The current stack of transformations
     * @param builder       The builder to put the data into
     * @param packedLight   The packed uv into the light texture the parts should be rendered at
     * @param packedOverlay The packed uv into the overlay texture the parts should be rendered at
     * @param red           The red factor for color
     * @param green         The green factor for color
     * @param blue          The blue factor for color
     * @param alpha         The alpha factor for color
     */
    void render(String material, GeometryModelTexture texture, PoseStack matrixStack, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);

    /**
     * Resets all model angles to the default transformation.
     */
    void resetTransformation();

    /**
     * Copies the model angles to the currently selected part.
     *
     * @param parent    The parent model part being copied
     * @param modelPart The part to copy angles of
     */
    void copyAngles(@Nullable String parent, ModelPart modelPart);

    /**
     * Fetches a model part from the model.
     *
     * @param part The name of the part to get
     * @return An optional of the model part by that name
     */
    Optional<ModelPart> getModelPart(String part);

    /**
     * Fetches all model parts that are a child to the provided parent part.
     *
     * @param part The name of the part to get children from
     * @return All model parts copying angles from that part
     */
    ModelPart[] getChildRenderers(String part);

    /**
     * @return An array of all model parts
     */
    ModelPart[] getModelParts();

    /**
     * @return An array of all used part keys
     */
    String[] getParentModelKeys();

    /**
     * @return An array of all used material keys
     */
    String[] getMaterialKeys();

    /**
     * @return The width of the texture in pixels
     */
    float getTextureWidth();

    /**
     * @return The height of the texture in pixels
     */
    float getTextureHeight();

    /**
     * Fetches an {@link VertexConsumer} for the specified texture.
     *
     * @param buffer             The render type buffers
     * @param atlas              The atlas to get the textures from
     * @param texture            The texture to use
     * @param renderTypeConsumer Additional render type properties to apply or <code>null</code> for nothing
     * @return The buffer that should be used for the provided texture
     */
    default VertexConsumer getBuffer(MultiBufferSource buffer, GeometryAtlasTexture atlas, GeometryModelTexture texture, @Nullable Consumer<RenderType.CompositeState.CompositeStateBuilder> renderTypeConsumer) {
        return atlas.getSprite(texture.getLocation()).wrap(buffer.getBuffer(texture.getLayer().getRenderType(texture, atlas, renderTypeConsumer)));
    }
}
