package gg.moonflower.pollen.core.network.fabric;

import gg.moonflower.pollen.api.config.fabric.ConfigTracker;
import gg.moonflower.pollen.core.network.PollenClientPlayPacketHandlerImpl;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.core.network.login.PollenClientLoginPacketHandler;
import gg.moonflower.pollen.core.network.play.PollenClientPlayPacketHandler;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenMessagesImpl {

    public static void registerPlatformPackets() {
        PollenMessages.LOGIN.registerLogin(ClientboundSyncConfigDataPacket.class, ClientboundSyncConfigDataPacket::new, ConfigTracker.INSTANCE::syncConfigs);
    }

    public static PollenClientPlayPacketHandler createClientPlayHandler() {
        return new PollenClientPlayPacketHandlerImpl();
    }

    public static PollenClientLoginPacketHandler createClientLoginHandler() {
        return new FabricClientLoginPacketHandlerImpl();
    }
}
