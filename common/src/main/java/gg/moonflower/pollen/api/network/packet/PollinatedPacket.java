package gg.moonflower.pollen.api.network.packet;

import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;

/**
 * A message intended for the specified message handler.
 *
 * @param <T> The interface that should handle this message
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedPacket<T> {

    /**
     * Writes the raw message data to the data stream.
     *
     * @param buf The buffer to write to
     */
    void writePacketData(FriendlyByteBuf buf) throws IOException;

    /**
     * Passes this message into the specified handler to process the message.
     *
     * @param handler The handler to process the message
     * @param ctx     The context of the message
     */
    void processPacket(T handler, PollinatedPacketContext ctx);
}
