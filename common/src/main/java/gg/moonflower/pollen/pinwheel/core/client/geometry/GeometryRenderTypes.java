package gg.moonflower.pollen.pinwheel.core.client.geometry;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public final class GeometryRenderTypes extends RenderType {

    private GeometryRenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType getGeometrySolid(GeometryModelTexture texture, ResourceLocation atlasLocation) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlasLocation, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(texture.isGlowing() ? NO_DIFFUSE_LIGHTING : DIFFUSE_LIGHTING).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_solid", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, rendertype$state);
    }

    public static RenderType getGeometryCutout(GeometryModelTexture texture, ResourceLocation atlasLocation) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlasLocation, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(texture.isGlowing() ? NO_DIFFUSE_LIGHTING : DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_cutout", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, rendertype$state);
    }

    public static RenderType getGeometryCutoutCull(GeometryModelTexture texture, ResourceLocation atlasLocation) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlasLocation, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(texture.isGlowing() ? NO_DIFFUSE_LIGHTING : DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_cutout_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, rendertype$state);
    }

    public static RenderType getGeometryTranslucent(GeometryModelTexture texture, ResourceLocation atlasLocation) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlasLocation, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(texture.isGlowing() ? NO_DIFFUSE_LIGHTING : DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_translucent", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, rendertype$state);
    }

    public static RenderType getGeometryTranslucentCull(GeometryModelTexture texture, ResourceLocation atlasLocation) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlasLocation, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(texture.isGlowing() ? NO_DIFFUSE_LIGHTING : DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_translucent_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, rendertype$state);
    }
}
