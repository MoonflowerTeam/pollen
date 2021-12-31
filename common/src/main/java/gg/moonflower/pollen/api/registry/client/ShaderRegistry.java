package gg.moonflower.pollen.api.registry.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ShaderRegistry {

    private static final Map<ResourceLocation, VertexFormat> REGISTERED_SHADERS = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, ShaderInstance> SHADERS = new HashMap<>();

    @ApiStatus.Internal
    public static void loadShader(ResourceLocation shader, ShaderInstance instance) {
        SHADERS.put(shader, instance);
    }

    public static void register(ResourceLocation shader, VertexFormat format) {
        REGISTERED_SHADERS.put(shader, format);
    }

    public static Supplier<ShaderInstance> getShader(ResourceLocation shader) {
        return () -> SHADERS.get(shader);
    }

    public static Set<Map.Entry<ResourceLocation, VertexFormat>> getRegisteredShaders() {
        return REGISTERED_SHADERS.entrySet();
    }
}
