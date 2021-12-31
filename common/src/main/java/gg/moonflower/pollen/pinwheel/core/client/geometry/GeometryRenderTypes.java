package gg.moonflower.pollen.pinwheel.core.client.geometry;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import gg.moonflower.pollen.api.registry.client.ShaderRegistry;
import gg.moonflower.pollen.core.client.render.PollenShaderTypes;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public final class GeometryRenderTypes extends RenderType {

    private static final RenderStateShard.ShaderStateShard RENDERTYPE_GEOMETRY_SOLID = new RenderStateShard.ShaderStateShard(ShaderRegistry.getShader(PollenShaderTypes.RENDERTYPE_GEOMETRY_SOLID));
    private static final RenderStateShard.ShaderStateShard RENDERTYPE_GEOMETRY_CUTOUT = new RenderStateShard.ShaderStateShard(ShaderRegistry.getShader(PollenShaderTypes.RENDERTYPE_GEOMETRY_CUTOUT));
    private static final RenderStateShard.ShaderStateShard RENDERTYPE_GEOMETRY_CUTOUT_CULL = new RenderStateShard.ShaderStateShard(ShaderRegistry.getShader(PollenShaderTypes.RENDERTYPE_GEOMETRY_CUTOUT_CULL));
    private static final RenderStateShard.ShaderStateShard RENDERTYPE_GEOMETRY_TRANSLUCENT = new RenderStateShard.ShaderStateShard(ShaderRegistry.getShader(PollenShaderTypes.RENDERTYPE_GEOMETRY_TRANSLUCENT));
    private static final RenderStateShard.ShaderStateShard RENDERTYPE_GEOMETRY_TRANSLUCENT_CULL = new RenderStateShard.ShaderStateShard(ShaderRegistry.getShader(PollenShaderTypes.RENDERTYPE_GEOMETRY_TRANSLUCENT_CULL));

    private GeometryRenderTypes(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType getGeometrySolid(ResourceLocation locationIn) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(locationIn, false, false)).setShaderState(RENDERTYPE_GEOMETRY_SOLID).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_solid", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
    }

    public static RenderType getGeometryCutout(ResourceLocation locationIn) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(locationIn, false, false)).setShaderState(RENDERTYPE_GEOMETRY_CUTOUT).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
    }

    public static RenderType getGeometryCutoutCull(ResourceLocation locationIn) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(locationIn, false, false)).setShaderState(RENDERTYPE_GEOMETRY_CUTOUT_CULL).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_cutout_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
    }

    public static RenderType getGeometryTranslucent(ResourceLocation locationIn) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(locationIn, false, false)).setShaderState(RENDERTYPE_GEOMETRY_TRANSLUCENT).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state);
    }

    public static RenderType getGeometryTranslucentCull(ResourceLocation locationIn) {
        CompositeState rendertype$state = CompositeState.builder().setTextureState(new TextureStateShard(locationIn, false, false)).setShaderState(RENDERTYPE_GEOMETRY_TRANSLUCENT_CULL).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
        return create("geometry_translucent_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state);
    }
}
