package gg.moonflower.pollen.core.mixin.fabric.network;

import gg.moonflower.pollen.core.extensions.fabric.ServerLoginPacketListenerImplExtension;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Unique
    private ServerLoginPacketListenerImplExtension extension;

    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    public void capturePacketListener(Connection connection, ServerPlayer player, CallbackInfo ci) {
        if (connection.getPacketListener() instanceof ServerLoginPacketListenerImplExtension extension) {
            this.extension = extension;
        }
    }

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundChangeDifficultyPacket;<init>(Lnet/minecraft/world/Difficulty;Z)V"))
    public void flushLoginPackets(Connection connection, ServerPlayer player, CallbackInfo ci) {
        if (this.extension != null) {
            this.extension.pollen$flushDelayedPackets(player.connection);
            this.extension = null;
        }
    }
}
