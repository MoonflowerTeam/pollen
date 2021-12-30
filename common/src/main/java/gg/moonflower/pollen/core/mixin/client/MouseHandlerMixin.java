package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.core.extensions.MouseHandlerExtension;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin implements MouseHandlerExtension {

    private double xOffset;

    @Inject(method = "onScroll", at = @At("HEAD"))
    public void onScroll(long handle, double xOffset, double yOffset, CallbackInfo ci) {
        this.xOffset = xOffset;
    }

    @Override
    public double pollen_getXOffset() {
        return xOffset;
    }
}
