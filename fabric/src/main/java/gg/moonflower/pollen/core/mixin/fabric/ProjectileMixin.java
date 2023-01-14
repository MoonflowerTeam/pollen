package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.events.entity.ProjectileImpactEvent;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public class ProjectileMixin {

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    public void onHit(HitResult hitResult, CallbackInfo ci) {
        if (!ProjectileImpactEvent.EVENT.invoker().onProjectileImpact((Projectile) (Object) this, hitResult))
            ci.cancel();
    }
}
