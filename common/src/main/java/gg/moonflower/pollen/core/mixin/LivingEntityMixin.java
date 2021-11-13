package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.event.EventDispatcher;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        EventDispatcher.post(new TickEvent.LivingEntityEvent.Post((LivingEntity) (Object) this));
    }
}
