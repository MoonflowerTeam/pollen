package gg.moonflower.pollen.core.network.play;

import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacketContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public interface PollenClientMessageHandler {

    void handleSetAnimation(ClientboundSetAnimationPacket packet, PollinatedPacketContext ctx);
}
