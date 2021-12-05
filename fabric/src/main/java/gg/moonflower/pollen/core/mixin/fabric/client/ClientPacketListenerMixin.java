package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.event.events.network.ClientNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
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

    @Inject(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;setServerBrand(Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void handleRespawn(ClientboundRespawnPacket pkt, CallbackInfo ci, ResourceKey<Level> dimensionKey, DimensionType dimensionType, LocalPlayer oldPlayer, int oldPlayerId, String serverBrand, LocalPlayer newPlayer) {
        ClientNetworkEvent.RESPAWN.invoker().respawn(this.minecraft.gameMode, oldPlayer, newPlayer, newPlayer.connection.getConnection());
    }
}
