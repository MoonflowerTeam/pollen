package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.entity.PollenEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Inject(method = "addEntity", at = @At("TAIL"))
    public void addEntity(int entityId, Entity entityToSpawn, CallbackInfo ci) {
        if (entityToSpawn instanceof PollenEntity)
            ((PollenEntity) entityToSpawn).onAddedToWorld();
    }

    @Inject(method = "onEntityRemoved", at = @At("TAIL"))
    public void onEntityRemoved(Entity entity, CallbackInfo ci) {
        if (entity instanceof PollenEntity)
            ((PollenEntity) entity).onRemovedFromWorld();
    }
}
