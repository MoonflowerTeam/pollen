package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.event.events.client.InputEvents;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardInputMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("TAIL"))
    public void keyPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (windowPointer == this.minecraft.getWindow().getWindow())
            InputEvents.KEY_INPUT_EVENT.invoker().keyInput(key, scanCode, action, modifiers);
    }
}
