package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {

    @Inject(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/GameProfileCache;setUsesAuthentication(Z)V", shift = At.Shift.AFTER), cancellable = true)
    public void preStart(CallbackInfoReturnable<Boolean> cir) {
        if (!ServerLifecycleEvents.PRE_STARTING.invoker().preStarting((MinecraftServer) (Object) this))
            cir.setReturnValue(false);
    }

    @Inject(method = "initServer", at = @At("TAIL"), cancellable = true)
    public void starting(CallbackInfoReturnable<Boolean> cir) {
        if (!ServerLifecycleEvents.STARTING.invoker().starting((MinecraftServer) (Object) this))
            cir.setReturnValue(false);
    }
}
