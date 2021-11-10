package gg.moonflower.pollen.api.network.packet.login;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import net.minecraft.network.FriendlyByteBuf;

/**
 * A blank packet for sending a simple response back to the server.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class ServerboundAckPacket extends SimplePollinatedLoginPacket<Object> {

    public ServerboundAckPacket() {
    }

    public ServerboundAckPacket(FriendlyByteBuf buf) {
    }

    @Override
    public void writePacketData(FriendlyByteBuf buf) {
    }

    @Override
    public void processPacket(Object handler, PollinatedPacketContext ctx) {
    }
}
