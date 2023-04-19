package gg.moonflower.pollen.impl.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import gg.moonflower.pollen.api.registry.render.v1.ShaderRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenShaderTypes {

    public static final ResourceLocation MODEL_SOLID = new ResourceLocation(Pollen.MOD_ID, "rendertype_geometry_solid");
    public static final ResourceLocation MODEL_CUTOUT = new ResourceLocation(Pollen.MOD_ID, "rendertype_geometry_cutout");
    public static final ResourceLocation MODEL_CUTOUT_CULL = new ResourceLocation(Pollen.MOD_ID, "rendertype_geometry_cutout_cull");
    public static final ResourceLocation MODEL_TRANSLUCENT = new ResourceLocation(Pollen.MOD_ID, "rendertype_geometry_translucent");
    public static final ResourceLocation MODEL_TRANSLUCENT_CULL = new ResourceLocation(Pollen.MOD_ID, "rendertype_geometry_translucent_cull");
    public static final ResourceLocation PARTICLE_SOLID = new ResourceLocation(Pollen.MOD_ID, "rendertype_particle_solid");
    public static final ResourceLocation PARTICLE_CUTOUT = new ResourceLocation(Pollen.MOD_ID, "rendertype_particle_cutout");
    public static final ResourceLocation PARTICLE_TRANSLUCENT = new ResourceLocation(Pollen.MOD_ID, "rendertype_particle_translucent");

    public static void init() {
        ShaderRegistry.register(MODEL_SOLID, DefaultVertexFormat.NEW_ENTITY);
        ShaderRegistry.register(MODEL_CUTOUT, DefaultVertexFormat.NEW_ENTITY);
        ShaderRegistry.register(MODEL_CUTOUT_CULL, DefaultVertexFormat.NEW_ENTITY);
        ShaderRegistry.register(MODEL_TRANSLUCENT, DefaultVertexFormat.NEW_ENTITY);
        ShaderRegistry.register(MODEL_TRANSLUCENT_CULL, DefaultVertexFormat.NEW_ENTITY);
        ShaderRegistry.register(PARTICLE_SOLID, DefaultVertexFormat.NEW_ENTITY);
        ShaderRegistry.register(PARTICLE_CUTOUT, DefaultVertexFormat.NEW_ENTITY);
        ShaderRegistry.register(PARTICLE_TRANSLUCENT, DefaultVertexFormat.NEW_ENTITY);
    }
}
