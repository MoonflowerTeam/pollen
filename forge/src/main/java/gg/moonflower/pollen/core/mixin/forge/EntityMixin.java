package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.entity.PollenEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "onAddedToWorld", at = @At("TAIL"), remap = false)
    public void onAddedToWorld(CallbackInfo ci) {
        if (this instanceof PollenEntity)
            ((PollenEntity) this).onAddedToLevel();
    }

    @Inject(method = "onRemovedFromWorld", at = @At("TAIL"), remap = false)
    public void onRemovedFromWorld(CallbackInfo ci) {
        if (this instanceof PollenEntity)
            ((PollenEntity) this).onRemovedFromLevel();
    }
}
