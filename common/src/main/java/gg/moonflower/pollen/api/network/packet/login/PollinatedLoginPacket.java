package gg.moonflower.pollen.api.network.packet.login;

import gg.moonflower.pollen.api.network.packet.PollinatedPacket;

import java.util.function.IntSupplier;

/**
 * An implementation of {@link PollinatedPacket} intended for login messages.
 *
 * @param <T> The interface that should handle this message
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedLoginPacket<T> extends PollinatedPacket<T>, IntSupplier {

    /**
     * @return The index of this login message.
     */
    @Override
    int getAsInt();

    /**
     * Sets the index for the login message. Should not usually be called.
     *
     * @param index The new login index
     */
    void setLoginIndex(int index);
}
