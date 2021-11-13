package gg.moonflower.pollen.core.network.forge;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public interface ForgeClientPlayPacketHandler {

    void handleClientboundUpdateSyncedDataPacket(ClientboundUpdateSyncedDataPacket msg, PollinatedPacketContext ctx);
}
