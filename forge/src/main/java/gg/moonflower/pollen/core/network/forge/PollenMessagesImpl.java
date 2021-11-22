package gg.moonflower.pollen.core.network.forge;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.core.network.PollenClientLoginPacketHandlerImpl;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.core.network.login.PollenClientLoginPacketHandler;
import gg.moonflower.pollen.core.network.play.PollenClientPlayPacketHandler;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenMessagesImpl {

    public static void registerPlatformPackets() {
        PollenMessages.PLAY.register(ClientboundUpdateSyncedDataPacket.class, ClientboundUpdateSyncedDataPacket::new, PollinatedPacketDirection.PLAY_CLIENTBOUND);
    }

    public static PollenClientPlayPacketHandler createClientPlayHandler() {
        return new ForgeClientPlayPacketHandlerImpl();
    }

    public static PollenClientLoginPacketHandler createClientLoginHandler() {
        return new PollenClientLoginPacketHandlerImpl();
    }
}
