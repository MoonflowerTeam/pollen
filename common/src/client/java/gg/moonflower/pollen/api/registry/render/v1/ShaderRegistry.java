package gg.moonflower.pollen.api.registry.render.v1;

import com.mojang.blaze3d.vertex.VertexFormat;
import gg.moonflower.pollen.impl.render.shader.ShaderRegistryImpl;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Creates shaders that can be referenced by id instead of static field.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface ShaderRegistry {

    static void register(ResourceLocation shader, VertexFormat format) {
        ShaderRegistryImpl.register(shader, format);
    }

    static Supplier<ShaderInstance> getShader(ResourceLocation shader) {
        return ShaderRegistryImpl.getShader(shader);
    }
}
