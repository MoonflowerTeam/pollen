package gg.moonflower.pollen.impl.render.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import dev.architectury.event.events.client.ClientReloadShadersEvent;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class ShaderRegistryImpl {

    private static final Map<ResourceLocation, ShaderInstance> SHADERS = new HashMap<>();

    public static void register(ResourceLocation shader, VertexFormat format) {
        ClientReloadShadersEvent.EVENT.register((manager, sink) -> sink.registerShader(create(manager, shader, format), instance -> SHADERS.put(shader, instance)));
    }

    public static Supplier<ShaderInstance> getShader(ResourceLocation shader) {
        return () -> SHADERS.get(shader);
    }

    @ExpectPlatform
    public static ShaderInstance create(ResourceProvider resourceProvider, ResourceLocation name, VertexFormat vertexFormat) {
        return Pollen.expect();
    }
}
