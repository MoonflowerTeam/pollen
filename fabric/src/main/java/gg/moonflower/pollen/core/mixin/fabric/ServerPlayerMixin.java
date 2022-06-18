package gg.moonflower.pollen.core.mixin.fabric;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import gg.moonflower.pollen.api.event.events.entity.living.LivingEntityEvents;
import gg.moonflower.pollen.api.event.events.entity.player.ContainerEvents;
import gg.moonflower.pollen.api.event.events.entity.player.PlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "openMenu", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayer;containerMenu:Lnet/minecraft/world/inventory/AbstractContainerMenu;", ordinal = 1, shift = At.Shift.AFTER))
    public void openMenu(MenuProvider menuProvider, CallbackInfoReturnable<OptionalInt> cir) {
        ContainerEvents.OPEN.invoker().open(this, this.containerMenu);
    }

    @Inject(method = "openHorseInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;addSlotListener(Lnet/minecraft/world/inventory/ContainerListener;)V", shift = At.Shift.AFTER))
    public void openHorseInventory(AbstractHorse horse, Container inventory, CallbackInfo ci) {
        ContainerEvents.OPEN.invoker().open(this, this.containerMenu);
    }

    @Inject(method = "doCloseContainer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;removed(Lnet/minecraft/world/entity/player/Player;)V", shift = At.Shift.AFTER))
    public void doCloseContainer(CallbackInfo ci) {
        ContainerEvents.OPEN.invoker().open(this, this.containerMenu);
    }

    @Inject(method = "startSleepInBed", at = @At("HEAD"), cancellable = true)
    public void startSleepInBed(BlockPos bedPos, CallbackInfoReturnable<Either<BedSleepingProblem, Unit>> cir) {
        BedSleepingProblem result = PlayerEvents.START_SLEEPING.invoker().startSleeping((Player) (Object) this, bedPos);
        if (result != null)
            cir.setReturnValue(Either.left(result));
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(DamageSource damageSource, CallbackInfo ci) {
        if (!LivingEntityEvents.DEATH.invoker().death((LivingEntity) (Object) this, damageSource))
            ci.cancel();
    }
}
