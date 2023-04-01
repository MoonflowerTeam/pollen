package gg.moonflower.pollen.api.network.v1.packet.login;

import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacketContext;
import net.minecraft.network.FriendlyByteBuf;

/**
 * A blank packet for sending a simple response back to the server.
 *
 * @author Ocelot
 * @since 2.0.0
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
