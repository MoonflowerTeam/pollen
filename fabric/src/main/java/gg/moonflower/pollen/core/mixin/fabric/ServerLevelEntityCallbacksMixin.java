package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.entity.PollenEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/server/level/ServerLevel$EntityCallbacks")
public class ServerLevelEntityCallbacksMixin {

    @Inject(method = "onCreated(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
    public void onCreated(Entity entity, CallbackInfo ci) {
        if (entity instanceof PollenEntity)
            ((PollenEntity) entity).onAddedToLevel();
    }

    @Inject(method = "onDestroyed(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
    public void onDestroyed(Entity entity, CallbackInfo ci) {
        if (entity instanceof PollenEntity)
            ((PollenEntity) entity).onRemovedFromLevel();
    }
}
