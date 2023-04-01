package gg.moonflower.pollen.api.network.v1;

import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacket;
import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;

/**
 * Deserializes packets from a {@link FriendlyByteBuf}.
 *
 * @param <MSG> The type of packet to deserialize
 * @param <T>   The handler interface for that packet
 * @since 2.0.0
 */
@FunctionalInterface
public interface PacketDeserializer<MSG extends PollinatedPacket<T>, T> {

    /**
     * Creates a new packet from the specified buffer.
     *
     * @param buf The buffer to read network data from
     * @return A new packet from network
     * @throws IOException If there is any error with the data
     */
    MSG deserialize(FriendlyByteBuf buf) throws IOException;
}
