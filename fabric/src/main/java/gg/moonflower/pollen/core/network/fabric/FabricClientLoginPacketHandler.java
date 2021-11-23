package gg.moonflower.pollen.core.network.fabric;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public interface FabricClientLoginPacketHandler {

    void handleClientboundSyncConfigDataPacket(ClientboundSyncConfigDataPacket msg, PollinatedPacketContext ctx);
}
