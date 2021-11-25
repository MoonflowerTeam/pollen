package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.entity.PollenEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "add", at = @At("TAIL"))
    public void add(Entity entity, CallbackInfo ci) {
        if (entity instanceof PollenEntity)
            ((PollenEntity) entity).onAddedToLevel();
    }

    @Inject(method = "onEntityRemoved", at = @At("TAIL"))
    public void onEntityRemoved(Entity entity, CallbackInfo ci) {
        if (entity instanceof PollenEntity)
            ((PollenEntity) entity).onRemovedFromLevel();
    }
}
