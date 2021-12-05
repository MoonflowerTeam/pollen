package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.event.events.entity.player.server.ServerPlayerTrackingEvents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "trackChunk", at = @At("HEAD"))
    public void trackChunk(ChunkPos chunkPos, Packet<?> packet, CallbackInfo ci) {
        ServerPlayerTrackingEvents.START_TRACKING_CHUNK.invoker().startTracking((ServerPlayer) (Object) this, chunkPos);
    }

    @Inject(method = "untrackChunk", at = @At("HEAD"))
    public void untrackChunk(ChunkPos chunkPos, CallbackInfo ci) {
        ServerPlayerTrackingEvents.STOP_TRACKING_CHUNK.invoker().stopTracking((ServerPlayer) (Object) this, chunkPos);
    }
}
