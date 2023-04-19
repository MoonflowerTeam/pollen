package gg.moonflower.pollen.impl.mixin;

import gg.moonflower.pollen.api.event.level.v1.ChunkTrackingEvent;
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
        ChunkTrackingEvent.START_TRACKING.invoker().event((ServerPlayer) (Object) this, chunkPos);
    }

    @Inject(method = "untrackChunk", at = @At("HEAD"))
    public void untrackChunk(ChunkPos chunkPos, CallbackInfo ci) {
        ChunkTrackingEvent.STOP_TRACKING.invoker().event((ServerPlayer) (Object) this, chunkPos);
    }
}
