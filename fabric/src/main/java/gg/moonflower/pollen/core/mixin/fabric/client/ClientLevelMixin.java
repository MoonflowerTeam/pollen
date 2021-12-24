package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.entity.PollenEntity;
import gg.moonflower.pollen.api.event.events.entity.EntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Shadow
    protected abstract LevelEntityGetter<Entity> getEntities();

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void addEntity(int entityId, Entity entityToSpawn, CallbackInfo ci) {
        if (!EntityEvents.JOIN.invoker().onJoin(entityToSpawn, (ClientLevel) (Object) this))
            ci.cancel();
    }

    @Inject(method = "addEntity", at = @At("TAIL"))
    public void onAddedToLevel(int entityId, Entity entityToSpawn, CallbackInfo ci) {
        if (entityToSpawn instanceof PollenEntity)
            ((PollenEntity) entityToSpawn).onAddedToLevel();
    }

    @Inject(method = "removeEntity", at = @At("TAIL"))
    public void onRemovedFromLevel(int entityId, Entity.RemovalReason reason, CallbackInfo ci) {
        Entity entity = this.getEntities().get(entityId);
        if (entity instanceof PollenEntity)
            ((PollenEntity) entity).onRemovedFromLevel();
    }
}
