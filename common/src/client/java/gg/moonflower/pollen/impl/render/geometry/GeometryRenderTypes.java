package gg.moonflower.pollen.impl.render.geometry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import gg.moonflower.pinwheel.api.texture.ModelTextureKey;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryAtlasTexture;
import gg.moonflower.pollen.api.render.shader.v1.ShaderRegistry;
import gg.moonflower.pollen.impl.render.shader.PollenShaderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public final class GeometryRenderTypes extends RenderType {

    private static final Map<Integer, RenderType> ENTITY_RENDER_TYPES = new HashMap<>();
    private static final Map<Integer, RenderType> PARTICLE_RENDER_TYPES = new HashMap<>();

    private static ShaderStateShard shader(ModelTextureKey texture, ResourceLocation shaderId) {
        return new ShaderStateShard(ShaderRegistry.getShader(shaderId)) {
            @Override
            public void setupRenderState() {
                super.setupRenderState();
                Objects.requireNonNull(RenderSystem.getShader()).safeGetUniform("Glowing").set(texture.glowing() ? 1 : 0);
            }
        };
    }

    private static @NotNull TextureStateShard texture(@NotNull GeometryAtlasTexture atlas) {
        return new TextureStateShard(atlas.getAtlasLocation(), false, false);
    }

    private GeometryRenderTypes(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }


    private static RenderType entitySolid(ModelTextureKey texture, GeometryAtlasTexture atlas, boolean cull, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        int hash = Objects.hash(texture, atlas, cull, consumer);
        return ENTITY_RENDER_TYPES.computeIfAbsent(hash, h -> {
            CompositeState.CompositeStateBuilder state =
                    CompositeState.builder()
                            .setTextureState(texture(atlas))
                            .setShaderState(shader(texture, PollenShaderTypes.MODEL_SOLID))
                            .setTransparencyState(NO_TRANSPARENCY)
                            .setCullState(cull ? CULL : NO_CULL)
                            .setLightmapState(LIGHTMAP)
                            .setOverlayState(OVERLAY);
            if (consumer != null) {
                consumer.accept(state);
            }
            return create("geometry_solid",
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    256,
                    true,
                    false,
                    state.createCompositeState(true));
        });
    }

    private static RenderType entityCutout(ModelTextureKey texture, GeometryAtlasTexture atlas, boolean cull, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        int hash = Objects.hash(texture, atlas, cull, consumer);
        return ENTITY_RENDER_TYPES.computeIfAbsent(hash, h -> {
            CompositeState.CompositeStateBuilder state =
                    CompositeState.builder()
                            .setTextureState(texture(atlas))
                            .setShaderState(shader(texture, cull ? PollenShaderTypes.MODEL_CUTOUT_CULL : PollenShaderTypes.MODEL_CUTOUT))
                            .setTransparencyState(NO_TRANSPARENCY)
                            .setCullState(cull ? CULL : NO_CULL)
                            .setLightmapState(LIGHTMAP)
                            .setOverlayState(OVERLAY);
            if (consumer != null) {
                consumer.accept(state);
            }
            return create("geometry_cutout",
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    256,
                    true,
                    false,
                    state.createCompositeState(true));
        });
    }

    private static RenderType entityTranslucent(ModelTextureKey texture, GeometryAtlasTexture atlas, boolean cull, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        int hash = Objects.hash(texture, atlas, cull, consumer);
        return ENTITY_RENDER_TYPES.computeIfAbsent(hash, h -> {
            CompositeState.CompositeStateBuilder state =
                    CompositeState.builder()
                            .setTextureState(texture(atlas))
                            .setShaderState(shader(texture, cull ? PollenShaderTypes.MODEL_TRANSLUCENT_CULL : PollenShaderTypes.MODEL_TRANSLUCENT))
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                            .setCullState(cull ? CULL : NO_CULL)
                            .setLightmapState(LIGHTMAP)
                            .setOverlayState(OVERLAY);
            if (consumer != null) {
                consumer.accept(state);
            }
            return create("geometry_translucent",
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    256,
                    true,
                    true,
                    state.createCompositeState(true));
        });
    }

    private static RenderType particleSolid(ModelTextureKey texture, GeometryAtlasTexture atlas, boolean cull, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        int hash = Objects.hash(texture, atlas, cull, consumer);
        return PARTICLE_RENDER_TYPES.computeIfAbsent(hash, h -> {
            CompositeState.CompositeStateBuilder state =
                    CompositeState.builder()
                            .setTextureState(texture(atlas))
                            .setShaderState(shader(texture, PollenShaderTypes.PARTICLE_SOLID))
                            .setTransparencyState(NO_TRANSPARENCY)
                            .setCullState(cull ? CULL : NO_CULL)
                            .setLightmapState(LIGHTMAP);
            if (consumer != null) {
                consumer.accept(state);
            }
            return create("particle_solid",
                    DefaultVertexFormat.PARTICLE,
                    VertexFormat.Mode.QUADS,
                    256,
                    true,
                    false,
                    state.createCompositeState(true));
        });
    }

    private static RenderType particleCutout(ModelTextureKey texture, GeometryAtlasTexture atlas, boolean cull, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        int hash = Objects.hash(texture, atlas, cull, consumer);
        PARTICLE_RENDER_TYPES.clear();
        return PARTICLE_RENDER_TYPES.computeIfAbsent(hash, h -> {
            CompositeState.CompositeStateBuilder state =
                    CompositeState.builder()
                            .setTextureState(texture(atlas))
                            .setShaderState(shader(texture, PollenShaderTypes.PARTICLE_CUTOUT))
                            .setTransparencyState(NO_TRANSPARENCY)
                            .setCullState(cull ? CULL : NO_CULL)
                            .setLightmapState(LIGHTMAP);
            if (consumer != null) {
                consumer.accept(state);
            }
            return create("particle_cutout",
                    DefaultVertexFormat.PARTICLE,
                    VertexFormat.Mode.QUADS,
                    256,
                    true,
                    false,
                    state.createCompositeState(true));
        });
    }

    private static RenderType particleTranslucent(ModelTextureKey texture, GeometryAtlasTexture atlas, boolean cull, @Nullable Consumer<CompositeState.CompositeStateBuilder> consumer) {
        int hash = Objects.hash(texture, atlas, cull, consumer);
        return PARTICLE_RENDER_TYPES.computeIfAbsent(hash, h -> {
            CompositeState.CompositeStateBuilder state =
                    CompositeState.builder()
                            .setTextureState(texture(atlas))
                            .setShaderState(shader(texture, PollenShaderTypes.PARTICLE_TRANSLUCENT))
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                            .setCullState(cull ? CULL : NO_CULL)
                            .setLightmapState(LIGHTMAP)
                            .setWriteMaskState(COLOR_WRITE);
            if (consumer != null) {
                consumer.accept(state);
            }
            return create("particle_translucent",
                    DefaultVertexFormat.PARTICLE,
                    VertexFormat.Mode.QUADS,
                    256,
                    true,
                    true,
                    state.createCompositeState(true));
        });
    }

    /**
     * Retrieves the render type for the specified texture and atlas.
     *
     * @param texture  The texture to get the render type for
     * @param atlas    The atlas to use
     * @param consumer An optional consumer to modify the render type
     * @return The render type to use for this texture
     */
    public static RenderType entity(ModelTextureKey texture, GeometryAtlasTexture atlas, @Nullable Consumer<RenderType.CompositeState.CompositeStateBuilder> consumer) {
        Objects.requireNonNull(texture, "texture");
        Objects.requireNonNull(atlas, "atlas");
        return switch (Objects.requireNonNull(texture).layer()) {
            case SOLID -> entitySolid(texture, atlas, false, consumer);
            case SOLID_CULL -> entitySolid(texture, atlas, true, consumer);
            case CUTOUT -> entityCutout(texture, atlas, false, consumer);
            case CUTOUT_CULL -> entityCutout(texture, atlas, true, consumer);
            case TRANSLUCENT -> entityTranslucent(texture, atlas, false, consumer);
            case TRANSLUCENT_CULL -> entityTranslucent(texture, atlas, true, consumer);
        };
    }

    /**
     * Retrieves the render type for the specified texture and atlas.
     *
     * @param texture  The texture to get the render type for
     * @param atlas    The atlas to use
     * @param consumer An optional consumer to modify the render type
     * @return The render type to use for this texture
     */
    public static RenderType particle(ModelTextureKey texture, GeometryAtlasTexture atlas, @Nullable Consumer<RenderType.CompositeState.CompositeStateBuilder> consumer) {
        Objects.requireNonNull(texture, "texture");
        Objects.requireNonNull(atlas, "atlas");
        return switch (Objects.requireNonNull(texture).layer()) {
            case SOLID -> particleSolid(texture, atlas, false, consumer);
            case SOLID_CULL -> particleSolid(texture, atlas, true, consumer);
            case CUTOUT -> particleCutout(texture, atlas, false, consumer);
            case CUTOUT_CULL -> particleCutout(texture, atlas, true, consumer);
            case TRANSLUCENT -> particleTranslucent(texture, atlas, false, consumer);
            case TRANSLUCENT_CULL -> particleTranslucent(texture, atlas, true, consumer);
        };
    }
}
