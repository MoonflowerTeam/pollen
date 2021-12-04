package gg.moonflower.pollen.api.client.shader;

import net.minecraft.resources.ResourceLocation;

/**
 * Transforms shader source immediately before creating a shader instance.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface ShaderPreProcessor {

    /**
     * Modifies the provided shader source.
     *
     * @param id   The id of the shader being loaded. Only useful if being used as a global pre-processor
     * @param data The data from the previous processing step
     * @param type The type of shader being loaded
     * @return Modified source code for the shader
     */
    String modify(ResourceLocation id, String data, ShaderProgram.Shader type);
}
