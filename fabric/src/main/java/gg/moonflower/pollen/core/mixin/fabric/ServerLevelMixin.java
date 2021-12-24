package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.events.world.ExplosionEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Explosion;explode()V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void explode(Entity entity, DamageSource damageSource, ExplosionDamageCalculator explosionDamageCalculator, double d, double e, double f, float g, boolean bl, Explosion.BlockInteraction blockInteraction, CallbackInfoReturnable<Explosion> cir, Explosion explosion) {
        if (!ExplosionEvents.START.invoker().start((ServerLevel) (Object) this, explosion))
            cir.setReturnValue(explosion);
    }
}
