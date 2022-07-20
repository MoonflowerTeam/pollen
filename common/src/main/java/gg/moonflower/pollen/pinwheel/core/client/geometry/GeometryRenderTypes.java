package gg.moonflower.pollen.pinwheel.core.client.geometry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.registry.client.ShaderRegistry;
import gg.moonflower.pollen.core.client.render.PollenShaderTypes;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryAtlasTexture;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import net.minecraft.client.renderer.RenderStateShard;
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

    private static RenderStateShard.ShaderStateShard getShader(GeometryModelTexture texture, ResourceLocation shaderId) {
        return new RenderStateShard.ShaderStateShard(ShaderRegistry.getShader(shaderId)) {
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
        return wrap(wrap(create("geometry_solid", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state.createCompositeState(true))));
    }

    public static RenderType getGeometryCutout(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_CUTOUT)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return wrap(wrap(create("geometry_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state.createCompositeState(true))));
    }

    public static RenderType getGeometryCutoutCull(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_CUTOUT_CULL)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return wrap(create("geometry_cutout_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state.createCompositeState(true)));
    }

    public static RenderType getGeometryTranslucent(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_TRANSLUCENT)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return wrap(create("geometry_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state.createCompositeState(true)));
    }

    public static RenderType getGeometryTranslucentCull(GeometryModelTexture texture, GeometryAtlasTexture atlas, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        CompositeState.CompositeStateBuilder rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlas.getAtlasLocation(), false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_TRANSLUCENT_CULL)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        if (consumer != null)
            consumer.accept(rendertype$state);
        return wrap(create("geometry_translucent_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state.createCompositeState(true)));
    }

    public static RenderType wrap(RenderType renderType) {
        return renderType;
    }
}
