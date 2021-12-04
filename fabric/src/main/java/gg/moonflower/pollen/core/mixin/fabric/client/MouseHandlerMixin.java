package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.event.events.client.InputEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDD)Z", shift = At.Shift.BEFORE), cancellable = true)
    public void onGuiMouseScrolledPre(long handle, double xOffset, double yOffset, CallbackInfo ci) {
        if (InputEvents.GUI_MOUSE_SCROLL_EVENT_PRE.invoker().mouseScrolled((MouseHandler) (Object) this, xOffset, yOffset))
            ci.cancel();
    }

    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDD)Z", shift = At.Shift.AFTER))
    public void onGuiMouseScrolledPost(long handle, double xOffset, double yOffset, CallbackInfo ci) {
        InputEvents.GUI_MOUSE_SCROLL_EVENT_POST.invoker().mouseScrolled((MouseHandler) (Object) this, xOffset, yOffset);
    }

    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z", shift = At.Shift.BEFORE), cancellable = true)
    public void onMouseScrolled(long handle, double xOffset, double yOffset, CallbackInfo ci) {
        if (InputEvents.MOUSE_SCROLL_EVENT.invoker().mouseScrolled((MouseHandler) (Object) this, xOffset, yOffset))
            ci.cancel();
    }

    @Inject(method = "onPress", at = @At("TAIL"))
    public void onPress(long handle, int button, int action, int mods, CallbackInfo ci) {
        if (handle == this.minecraft.getWindow().getWindow())
            InputEvents.MOUSE_INPUT_EVENT.invoker().mouseInput((MouseHandler) (Object) this, button, action, mods);
    }
}
