package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.api.event.events.entity.player.server.PlayerTrackingEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.BitSet;

@Mixin(ClientChunkCache.class)
public class ClientChunkCacheMixin {

    @Inject(method = "replaceWithPacketData", at = @At("TAIL"))
    public void trackChunk(int i, int j, ChunkBiomeContainer chunkBiomeContainer, FriendlyByteBuf friendlyByteBuf, CompoundTag compoundTag, BitSet bitSet, CallbackInfoReturnable<LevelChunk> cir) {
        PlayerTrackingEvent.START_TRACKING_CHUNK.invoker().track(Minecraft.getInstance().player, cir.getReturnValue().getPos());
    }

    @Inject(method = "drop", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;getChunk(I)Lnet/minecraft/world/level/chunk/LevelChunk;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void untrackChunk(int i, int j, CallbackInfo ci, int index, LevelChunk chunk) {
        PlayerTrackingEvent.STOP_TRACKING_CHUNK.invoker().track(Minecraft.getInstance().player, chunk.getPos());
    }
}
