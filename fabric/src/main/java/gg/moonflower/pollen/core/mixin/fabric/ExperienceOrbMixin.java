package gg.moonflower.pollen.core.mixin.fabric;


import gg.moonflower.pollen.api.event.events.entity.player.PlayerEvents;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;take(Lnet/minecraft/world/entity/Entity;I)V", shift = At.Shift.BEFORE), cancellable = true)
    public void playerTouch(Player player, CallbackInfo ci) {
        if (!PlayerEvents.EXP_PICKUP.invoker().expPickup(player, (ExperienceOrb) (Object) this))
            ci.cancel();
    }
}
