package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J", ordinal = 0, shift = At.Shift.BEFORE))
    public void started(CallbackInfo ci) {
        ServerLifecycleEvents.STARTED.invoker().started((MinecraftServer) (Object) this);
    }
}
