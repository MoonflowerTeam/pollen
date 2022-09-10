package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.events.entity.living.LivingEntityEvents;
import gg.moonflower.pollen.api.event.events.entity.player.PlayerEvents;
import gg.moonflower.pollen.api.util.value.MutableFloat;
import gg.moonflower.pollen.api.util.value.MutableInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "stopSleepInBed", at = @At("HEAD"))
    public void stopSleepInBed(boolean wakeImmediatly, boolean updateLevelForSleepingPlayers, CallbackInfo ci) {
        PlayerEvents.STOP_SLEEPING.invoker().stopSleeping((Player) (Object) this, wakeImmediatly, updateLevelForSleepingPlayers);
    }

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setAbsorptionAmount(F)V", shift = At.Shift.AFTER), ordinal = 0, argsOnly = true)
    public float modifyDamageAmount(float value, DamageSource damageSource) {
        MutableFloat mutableDamage = MutableFloat.of(value);
        boolean event = LivingEntityEvents.DAMAGE.invoker().livingDamage((LivingEntity) (Object) this, damageSource, mutableDamage);
        return event ? mutableDamage.getAsFloat() : 0.0F;
    }

    @ModifyVariable(method = "giveExperiencePoints", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public int modifyExp(int value) {
        MutableInt mutableXp = MutableInt.of(value);
        boolean event = PlayerEvents.EXP_CHANGE.invoker().expChange((Player) (Object) this, mutableXp);
        return event ? mutableXp.getAsInt() : value;
    }

    @ModifyVariable(method = "giveExperienceLevels", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public int modifyLevels(int value) {
        MutableInt mutableLevels = MutableInt.of(value);
        boolean event = PlayerEvents.LEVEL_CHANGE.invoker().levelChange((Player) (Object) this, mutableLevels);
        return event ? mutableLevels.getAsInt() : value;
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(DamageSource damageSource, CallbackInfo ci) {
        if (!LivingEntityEvents.DEATH.invoker().death((LivingEntity) (Object) this, damageSource))
            ci.cancel();
    }
}
