package gg.moonflower.pollen.pinwheel.core.client.geometry;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryAtlasTexture;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public final class GeometryRenderTypes extends RenderType {

    private GeometryRenderTypes(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType getGeometrySolid(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(texture.isGlowing() ? NO_DIFFUSE_LIGHTING : DIFFUSE_LIGHTING).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return create("geometry_solid", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, rendertype$state.createCompositeState(true));
    }

    public static RenderType getGeometryCutout(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(texture.isGlowing() ? NO_DIFFUSE_LIGHTING : DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return create("geometry_cutout", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, rendertype$state.createCompositeState(true));
    }

    public static RenderType getGeometryCutoutCull(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(texture.isGlowing() ? NO_DIFFUSE_LIGHTING : DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return create("geometry_cutout_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, rendertype$state.createCompositeState(true));
    }

    public static RenderType getGeometryTranslucent(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(texture.isGlowing() ? NO_DIFFUSE_LIGHTING : DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return create("geometry_translucent", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, rendertype$state.createCompositeState(true));
    }

    public static RenderType getGeometryTranslucentCull(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(texture.isGlowing() ? NO_DIFFUSE_LIGHTING : DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return create("geometry_translucent_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, rendertype$state.createCompositeState(true));
    }
}
