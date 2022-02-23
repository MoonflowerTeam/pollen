package gg.moonflower.pollen.core.network.play;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public interface PollenServerPlayPacketHandler {

    void handleUpdateSettingsPacket(ServerboundUpdateSettingsPacket msg, PollinatedPacketContext ctx);
}
