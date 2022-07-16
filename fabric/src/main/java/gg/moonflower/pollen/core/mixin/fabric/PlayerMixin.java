package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.events.entity.living.LivingEntityEvents;
import gg.moonflower.pollen.api.event.events.entity.player.PlayerEvents;
import gg.moonflower.pollen.common.events.context.LivingDamageContextImpl;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

    @Unique
    private static DamageSource captureDamageSource;

    @Unique
    private static float captureDamageAmount;

    @Inject(method = "stopSleepInBed", at = @At("HEAD"))
    public void stopSleepInBed(boolean wakeImmediatly, boolean updateLevelForSleepingPlayers, CallbackInfo ci) {
        PlayerEvents.STOP_SLEEPING.invoker().stopSleeping((Player) (Object) this, wakeImmediatly, updateLevelForSleepingPlayers);
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    public void captureArgs(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
        captureDamageSource = damageSource;
        captureDamageAmount = damageAmount;
    }

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setAbsorptionAmount(F)V", shift = At.Shift.AFTER), ordinal = 0, argsOnly = true)
    public float modifyDamageAmount(float value) {
        LivingEntityEvents.Damage.Context context = new LivingDamageContextImpl(captureDamageAmount);
        boolean event = LivingEntityEvents.DAMAGE.invoker().livingDamage((LivingEntity) (Object) this, captureDamageSource, context);
        return event ? context.getDamageAmount() : 0.0F;
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(DamageSource damageSource, CallbackInfo ci) {
        if (!LivingEntityEvents.DEATH.invoker().death((LivingEntity) (Object) this, damageSource))
            ci.cancel();
    }
}
