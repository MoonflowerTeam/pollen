package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.event.events.entity.player.ModifyGravityEvent;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        TickEvent.LIVING_POST.invoker().tick((LivingEntity) (Object) this);
    }

    @ModifyVariable(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;", shift = At.Shift.BEFORE))
    public double modifyGravity(double gravity) {
        return ModifyGravityEvent.EVENT.invoker().modifyGravity((LivingEntity) (Object) this, gravity);
    }
}
