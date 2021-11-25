package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.blockentity.PollenBlockEntity;
import gg.moonflower.pollen.api.event.events.network.ClientNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.dimension.DimensionType;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow
    private Minecraft minecraft;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    private Connection connection;

    @Shadow
    private ClientLevel level;

    @Inject(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;setServerBrand(Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void handleRespawn(ClientboundRespawnPacket pkt, CallbackInfo ci, ResourceKey<Level> dimensionKey, DimensionType dimensionType, LocalPlayer oldPlayer, int oldPlayerId, String serverBrand, LocalPlayer newPlayer) {
        ClientNetworkEvent.RESPAWN.invoker().respawn(this.minecraft.gameMode, oldPlayer, newPlayer, newPlayer.connection.getConnection());
    }

    @Inject(method = "handleBlockEntityData", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void handleBlockEntityData(ClientboundBlockEntityDataPacket packet, CallbackInfo ci, BlockPos blockpos, BlockEntity blockEntity) {
        if (blockEntity == null) {
            LOGGER.error("Received invalid update packet for null tile entity at {} with data: {}", packet.getPos(), packet.getTag());
            return;
        }

        if (blockEntity instanceof PollenBlockEntity)
            ((PollenBlockEntity) blockEntity).onDataPacket(this.connection, packet);
    }

    @Inject(method = "handleLevelChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;load(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    public void handleLevelChunk(ClientboundLevelChunkPacket packet, CallbackInfo ci, int i, int j, ChunkBiomeContainer chunkBiomeContainer, CompoundTag compoundTag, BlockPos pos, BlockEntity blockEntity) {
        if (blockEntity instanceof PollenBlockEntity) {
            ci.cancel();
            ((PollenBlockEntity) blockEntity).handleUpdateTag(this.level.getBlockState(pos), compoundTag);
        }
    }
}
