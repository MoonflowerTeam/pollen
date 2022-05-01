package gg.moonflower.pollen.core.network.fabric;

import gg.moonflower.pollen.api.config.fabric.ConfigTracker;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.core.network.login.PollenClientLoginPacketHandler;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenMessagesImpl {

    public static void registerPlatformPackets() {
        PollenMessages.LOGIN.registerLogin(ClientboundSyncConfigDataPacket.class, ClientboundSyncConfigDataPacket::new, ConfigTracker.INSTANCE::syncConfigs);
    }

    public static PollenClientLoginPacketHandler createClientLoginHandler() {
        return new FabricClientLoginPacketHandlerImpl();
    }
}
