package gg.moonflower.pollen.api.registry.v1.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import gg.moonflower.pollen.impl.registry.render.ShaderRegistryImpl;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Registers custom shaders, usually for custom render types.
 *
 * @author Ocelot
 */
public interface ShaderRegistry {

    /**
     * Registers the specified shader.
     *
     * @param shader The shader location
     * @param format The Vertex Format to associate with. Used to know what variables are being input
     */
    static void register(ResourceLocation shader, VertexFormat format) {
        ShaderRegistryImpl.register(shader, format);
    }

    /**
     * Retrieves a shader by the specified name.
     *
     * @param shader The name of the shader
     * @return A supplier pointing to the shader
     */
    static Supplier<ShaderInstance> getShader(ResourceLocation shader) {
        return ShaderRegistryImpl.getShader(shader);
    }

    /**
     * @return All registered shaders
     */
    static Set<Map.Entry<ResourceLocation, VertexFormat>> getRegisteredShaders() {
        return ShaderRegistryImpl.getRegisteredShaders();
    }
}
