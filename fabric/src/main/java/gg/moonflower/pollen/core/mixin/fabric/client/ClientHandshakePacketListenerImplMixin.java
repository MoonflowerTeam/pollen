package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.config.fabric.ConfigTracker;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHandshakePacketListenerImpl.class)
public class ClientHandshakePacketListenerImplMixin {

    @Inject(method = "handleGameProfile", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setListener(Lnet/minecraft/network/PacketListener;)V", shift = At.Shift.BEFORE))
    public void handleGameProfile(ClientboundGameProfilePacket clientboundGameProfilePacket, CallbackInfo ci) {
        ConfigTracker.INSTANCE.loadDefaultServerConfigs();
    }
}
