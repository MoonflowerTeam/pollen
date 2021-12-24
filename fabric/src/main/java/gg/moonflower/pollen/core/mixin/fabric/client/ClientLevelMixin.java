package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.event.events.entity.EntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void addEntity(int entityId, Entity entityToSpawn, CallbackInfo ci) {
        if (!EntityEvents.JOIN.invoker().onJoin(entityToSpawn, (ClientLevel) (Object) this))
            ci.cancel();
    }

    @Inject(method = "onEntityRemoved", at = @At("TAIL"))
    private void onEntityRemoved(Entity entity, CallbackInfo ci) {
        EntityEvents.LEAVE.invoker().onLeave(entity, (ClientLevel) (Object) this);
    }
}
