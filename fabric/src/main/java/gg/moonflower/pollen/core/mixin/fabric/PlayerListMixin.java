package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.events.entity.player.PlayerEvents;
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
public class PlayerListMixin {

    @Unique
    private ServerLoginPacketListenerImplExtension extension;

    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    public void capturePacketListener(Connection connection, ServerPlayer player, CallbackInfo ci) {
        this.extension = (ServerLoginPacketListenerImplExtension) connection.getPacketListener();
    }

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundChangeDifficultyPacket;<init>(Lnet/minecraft/world/Difficulty;Z)V"))
    public void flushLoginPackets(Connection connection, ServerPlayer player, CallbackInfo ci) {
        this.extension.pollen_flushDelayedPackets(player.connection);
        this.extension = null;
    }

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    public void placeNewPlayer(Connection connection, ServerPlayer player, CallbackInfo ci) {
        PlayerEvents.LOGGED_IN_EVENT.invoker().playerLoggedIn(player);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    public void remove(ServerPlayer player, CallbackInfo ci) {
        PlayerEvents.LOGGED_OUT_EVENT.invoker().playerLoggedOut(player);
    }
}
