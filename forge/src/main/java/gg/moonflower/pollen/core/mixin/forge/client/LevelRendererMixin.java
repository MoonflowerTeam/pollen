package gg.moonflower.pollen.core.mixin.forge.client;

import gg.moonflower.pollen.api.event.events.client.render.ReloadRendersEvent;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Inject(method = "allChanged", at = @At("HEAD"))
    public void allChanged(CallbackInfo ci) {
        ReloadRendersEvent.EVENT.invoker().reloadRenders();
    }
}
