package gg.moonflower.pollen.client.mixin.fabric.config;

import gg.moonflower.pollen.impl.config.fabric.ConfigTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHandshakePacketListenerImpl.class)
public class ClientHandshakePacketListenerImplMixin {

    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique
    private boolean loadedServerConfigs;

    @Inject(method = "handleCustomQuery", at = @At("HEAD"))
    public void handleCustomQuery(ClientboundCustomQueryPacket packet, CallbackInfo ci) {
        if (!this.loadedServerConfigs && !this.minecraft.hasSingleplayerServer()) { // Don't override actual configs with client dummy data for the local player
            ConfigTracker.INSTANCE.loadDefaultServerConfigs();
            this.loadedServerConfigs = true; // This is to ensure the default configs are loaded before any config data is received
        }
    }
}
