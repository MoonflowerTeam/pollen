package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.FabricHooks;
import gg.moonflower.pollen.api.event.events.entity.living.LivingEntityEvents;
import gg.moonflower.pollen.api.event.events.entity.player.PlayerEvents;
import gg.moonflower.pollen.api.util.value.FloatValue;
import gg.moonflower.pollen.api.util.value.IntValue;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

    @Unique
    private DamageSource captureDamageSource;

    @Inject(method = "stopSleepInBed", at = @At("HEAD"))
    public void stopSleepInBed(boolean wakeImmediatly, boolean updateLevelForSleepingPlayers, CallbackInfo ci) {
        PlayerEvents.STOP_SLEEPING.invoker().stopSleeping((Player) (Object) this, wakeImmediatly, updateLevelForSleepingPlayers);
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    public void captureArgs(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
        captureDamageSource = damageSource;
    }

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setAbsorptionAmount(F)V", shift = At.Shift.AFTER), ordinal = 0, argsOnly = true)
    public float modifyDamageAmount(float value) {
        FloatValue modifiableDamage = new FloatValue.Simple(value);
        boolean event = LivingEntityEvents.DAMAGE.invoker().livingDamage((LivingEntity) (Object) this, captureDamageSource, modifiableDamage);
        return event ? modifiableDamage.getAsFloat() : 0.0F;
    }

    @ModifyVariable(method = "giveExperiencePoints", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public int modifyExp(int value) {
        IntValue modifiableXp = new IntValue.Simple(value);
        boolean event = PlayerEvents.EXP_CHANGE.invoker().expChange((Player) (Object) this, modifiableXp);
        return event ? modifiableXp.getAsInt() : value;
    }

    @ModifyVariable(method = "giveExperienceLevels", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public int modifyLevels(int value) {
        IntValue modifiableLevels = new IntValue.Simple(value);
        boolean event = PlayerEvents.LEVEL_CHANGE.invoker().levelChange((Player) (Object) this, modifiableLevels);
        return event ? modifiableLevels.getAsInt() : value;
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(DamageSource damageSource, CallbackInfo ci) {
        if (!LivingEntityEvents.DEATH.invoker().death((LivingEntity) (Object) this, damageSource))
            ci.cancel();
    }
}
