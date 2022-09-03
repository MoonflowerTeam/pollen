package gg.moonflower.pollen.impl.pinwheel.geometry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import gg.moonflower.pollen.api.pinwheel.v1.texture.GeometryModelTexture;
import gg.moonflower.pollen.api.pinwheel.v1.texture.GeometryAtlasTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public final class GeometryRenderTypes extends RenderType {

    private static ShaderStateShard getShader(GeometryModelTexture texture, ResourceLocation shaderId) {
        return new ShaderStateShard(ShaderRegistry.getShader(shaderId)) {
            @Override
            public void setupRenderState() {
                super.setupRenderState();
                Objects.requireNonNull(RenderSystem.getShader()).safeGetUniform("Glowing").set(texture.isGlowing() ? 1 : 0);
            }
        };
    }

    private GeometryRenderTypes(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType getGeometrySolid(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_SOLID)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return create("geometry_solid", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state.createCompositeState(true)));
    }

    public static RenderType getGeometryCutout(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_CUTOUT)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return create("geometry_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state.createCompositeState(true)));
    }

    public static RenderType getGeometryCutoutCull(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_CUTOUT_CULL)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return create("geometry_cutout_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state.createCompositeState(true));
    }

    public static RenderType getGeometryTranslucent(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_TRANSLUCENT)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return create("geometry_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state.createCompositeState(true));
    }

    public static RenderType getGeometryTranslucentCull(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_TRANSLUCENT_CULL)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return create("geometry_translucent_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state.createCompositeState(true));
    }
}
