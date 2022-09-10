package gg.moonflower.pollen.mixin.client;

import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.registry.v1.render.ShaderRegistry;
import gg.moonflower.pollen.impl.registry.render.ShaderRegistryImpl;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "reloadShaders", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;shutdownShaders()V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    public void reloadShaders(ResourceManager resourceManager, CallbackInfo ci, List<Program> list, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> list1) {
        try {
            for (Map.Entry<ResourceLocation, VertexFormat> entry : ShaderRegistry.getRegisteredShaders()) {
                list1.add(Pair.of(new ShaderInstance(resourceManager, entry.getKey().toString(), entry.getValue()), instance -> ShaderRegistryImpl.loadShader(entry.getKey(), instance)));
            }
        } catch (IOException e) {
            list1.forEach(pair -> pair.getFirst().close());
            throw new RuntimeException("could not reload shaders", e);
        }
    }
}
