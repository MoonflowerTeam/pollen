package gg.moonflower.pollen.core.network.login;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public interface PollenClientLoginPacketHandler {

    void handleSyncPlayerDataKeysPacket(ClientboundSyncPlayerDataKeysPacket pkt, PollinatedPacketContext ctx);
}
