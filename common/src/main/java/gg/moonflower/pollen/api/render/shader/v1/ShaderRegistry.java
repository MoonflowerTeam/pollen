package gg.moonflower.pollen.api.render.shader.v1;

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

    /**
     * Registers a new shader instance at the specified location.
     *
     * @param shader The location of the shader. This supports custom namespaces
     * @param format The vertex format of the shader
     */
    static void register(ResourceLocation shader, VertexFormat format) {
        ShaderRegistryImpl.register(shader, format);
    }

    /**
     * Retrieves a reference to the shader with the specified id.
     *
     * @param shader The name of the shader to get
     * @return A supplier pointing to that shader instance
     */
    static Supplier<ShaderInstance> getShader(ResourceLocation shader) {
        return ShaderRegistryImpl.getShader(shader);
    }
}
