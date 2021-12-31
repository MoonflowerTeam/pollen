package gg.moonflower.pollen.core.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import gg.moonflower.pollen.api.registry.client.ShaderRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenShaderTypes {

    public static final ResourceLocation RENDERTYPE_GEOMETRY_SOLID = new ResourceLocation(Pollen.MOD_ID, "rendertype_geometry_solid");
    public static final ResourceLocation RENDERTYPE_GEOMETRY_CUTOUT = new ResourceLocation(Pollen.MOD_ID, "rendertype_geometry_cutout");
    public static final ResourceLocation RENDERTYPE_GEOMETRY_CUTOUT_CULL = new ResourceLocation(Pollen.MOD_ID, "rendertype_geometry_cutout_cull");
    public static final ResourceLocation RENDERTYPE_GEOMETRY_TRANSLUCENT = new ResourceLocation(Pollen.MOD_ID, "rendertype_geometry_translucent");
    public static final ResourceLocation RENDERTYPE_GEOMETRY_TRANSLUCENT_CULL = new ResourceLocation(Pollen.MOD_ID, "rendertype_geometry_translucent_cull");

    public static void init() {
        ShaderRegistry.register(RENDERTYPE_GEOMETRY_SOLID, DefaultVertexFormat.NEW_ENTITY);
        ShaderRegistry.register(RENDERTYPE_GEOMETRY_CUTOUT, DefaultVertexFormat.NEW_ENTITY);
        ShaderRegistry.register(RENDERTYPE_GEOMETRY_CUTOUT_CULL, DefaultVertexFormat.NEW_ENTITY);
        ShaderRegistry.register(RENDERTYPE_GEOMETRY_TRANSLUCENT, DefaultVertexFormat.NEW_ENTITY);
        ShaderRegistry.register(RENDERTYPE_GEOMETRY_TRANSLUCENT_CULL, DefaultVertexFormat.NEW_ENTITY);
    }
}
