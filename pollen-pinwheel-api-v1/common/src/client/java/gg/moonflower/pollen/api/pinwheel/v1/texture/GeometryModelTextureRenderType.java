package gg.moonflower.pollen.api.pinwheel.v1.texture;

import gg.moonflower.pollen.impl.pinwheel.geometry.GeometryRenderTypes;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Retrieves the render type of {@link GeometryAtlasTexture}.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface GeometryModelTextureRenderType {

    /**
     * Fetches the render type for the specified location.
     *
     * @param texture            The texture to get a texture for
     * @param atlas              The texture atlas to use
     * @param renderTypeConsumer Additional properties to apply to the render type
     * @return The render type for this layer
     */
    static RenderType getRenderType(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<RenderType.CompositeState.CompositeStateBuilder> renderTypeConsumer) {
        return switch (texture.getLayer()) {
            case SOLID -> GeometryRenderTypes.getGeometrySolid(texture, atlas, renderTypeConsumer);
            case CUTOUT -> GeometryRenderTypes.getGeometryCutout(texture, atlas, renderTypeConsumer);
            case CUTOUT_CULL -> GeometryRenderTypes.getGeometryCutoutCull(texture, atlas, renderTypeConsumer);
            case TRANSLUCENT -> GeometryRenderTypes.getGeometryTranslucent(texture, atlas, renderTypeConsumer);
            case TRANSLUCENT_CULL -> GeometryRenderTypes.getGeometryTranslucentCull(texture, atlas, renderTypeConsumer);
        };
    }
}
