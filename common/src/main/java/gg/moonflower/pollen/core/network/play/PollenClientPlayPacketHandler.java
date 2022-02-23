package gg.moonflower.pollen.core.network.play;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public interface PollenClientPlayPacketHandler {

    void handleSyncAnimationPacket(ClientboundSyncAnimationPacket msg, PollinatedPacketContext ctx);

    void handleUpdateSettingsPacket(ClientboundUpdateSettingsPacket msg, PollinatedPacketContext ctx);
}
