package gg.moonflower.pollen.impl.render.shader.forge;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;

public class ShaderRegistryImplImpl {

    public static ShaderInstance create(ResourceProvider resourceProvider, ResourceLocation name, VertexFormat vertexFormat) {
        try {
            return new ShaderInstance(resourceProvider, name, vertexFormat);
        } catch (IOException e) {
            throw new RuntimeException("could not reload shader: " + name, e);
        }
    }
}
