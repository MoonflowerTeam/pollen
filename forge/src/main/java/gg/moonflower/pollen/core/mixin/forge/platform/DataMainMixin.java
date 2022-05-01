package gg.moonflower.pollen.core.mixin.forge.platform;

import gg.moonflower.pollen.api.platform.forge.PlatformImpl;
import net.minecraft.data.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Main.class, priority = 10000)
public class DataMainMixin {

    @Inject(method = "main", at = @At("HEAD"))
    private static void main(String[] args, CallbackInfo ci) {
        PlatformImpl.setArguments(args);
    }
}
