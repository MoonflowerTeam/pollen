package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.events.entity.EntityEvents;
import gg.moonflower.pollen.api.event.events.world.ExplosionEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Explosion;explode()V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void explode(Entity exploder, DamageSource damageSource, ExplosionDamageCalculator context, double x, double y, double z, float size, boolean causesFire, Explosion.BlockInteraction mode, CallbackInfoReturnable<Explosion> cir, Explosion explosion) {
        if (ExplosionEvents.START.invoker().start((ServerLevel) (Object) this, explosion))
            cir.setReturnValue(explosion);
    }

    @Inject(method = "addEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getChunk(IILnet/minecraft/world/level/chunk/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;", shift = At.Shift.BEFORE), cancellable = true)
    public void addEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (EntityEvents.JOIN.invoker().onJoin(entity, (ServerLevel) (Object) this))
            cir.setReturnValue(false);
    }

    @Inject(method = "addPlayer", at = @At(value = "HEAD"), cancellable = true)
    public void addPlayer(ServerPlayer player, CallbackInfo ci) {
        if (EntityEvents.JOIN.invoker().onJoin(player, (ServerLevel) (Object) this))
            ci.cancel();
    }

    @Inject(method = "loadFromChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;add(Lnet/minecraft/world/entity/Entity;)V", shift = At.Shift.BEFORE), cancellable = true)
    public void loadFromChunk(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (EntityEvents.JOIN.invoker().onJoin(entity, (ServerLevel) (Object) this))
            cir.setReturnValue(false);
    }

    @Inject(method = "onEntityRemoved", at = @At("TAIL"))
    private void onEntityRemoved(Entity entity, CallbackInfo ci) {
        EntityEvents.LEAVE.invoker().onLeave(entity, (ServerLevel) (Object) this);
    }
}
