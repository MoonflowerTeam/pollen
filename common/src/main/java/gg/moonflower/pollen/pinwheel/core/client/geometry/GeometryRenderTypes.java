package gg.moonflower.pollen.pinwheel.core.client.geometry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import gg.moonflower.pollen.api.registry.client.ShaderRegistry;
import gg.moonflower.pollen.core.client.render.PollenShaderTypes;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

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
                RenderSystem.getShader().safeGetUniform("Glowing").set(texture.isGlowing() ? 1 : 0);
            }
        };
    }

    private GeometryRenderTypes(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType getGeometrySolid(GeometryModelTexture texture, ResourceLocation atlasLocation) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlasLocation, false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_SOLID)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_solid", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
    }

    public static RenderType getGeometryCutout(GeometryModelTexture texture, ResourceLocation atlasLocation) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlasLocation, false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_CUTOUT)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
    }

    public static RenderType getGeometryCutoutCull(GeometryModelTexture texture, ResourceLocation atlasLocation) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlasLocation, false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_CUTOUT_CULL)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_cutout_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
    }

    public static RenderType getGeometryTranslucent(GeometryModelTexture texture, ResourceLocation atlasLocation) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlasLocation, false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_TRANSLUCENT)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state);
    }

    public static RenderType getGeometryTranslucentCull(GeometryModelTexture texture, ResourceLocation atlasLocation) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(atlasLocation, false, false)).setShaderState(getShader(texture, PollenShaderTypes.RENDERTYPE_GEOMETRY_TRANSLUCENT_CULL)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_translucent_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state);
    }
}
