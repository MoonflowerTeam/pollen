package gg.moonflower.pollen.core.mixin.forge.client;

import com.mojang.blaze3d.systems.RenderSystem;
import gg.moonflower.pollen.api.event.events.client.render.InitRendererEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {

    @Inject(method = "initRenderer", at = @At("TAIL"))
    private static void initRenderer(int debugVerbosity, boolean synchronous, CallbackInfo ci) {
        InitRendererEvent.EVENT.invoker().initRenderer();
    }
}
