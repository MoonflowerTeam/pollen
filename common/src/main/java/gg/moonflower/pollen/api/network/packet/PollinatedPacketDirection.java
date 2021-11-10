package gg.moonflower.pollen.api.network.packet;

/**
 * Used by the Pollen networking API to determine what direction packets are going. Based on forge's NetworkDirection.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public enum PollinatedPacketDirection {

    PLAY_SERVERBOUND, PLAY_CLIENTBOUND, LOGIN_SERVERBOUND, LOGIN_CLIENTBOUND;

    /**
     * @return Whether this direction is from server to client
     */
    public boolean isClientbound() {
        return this == PLAY_CLIENTBOUND || this == LOGIN_CLIENTBOUND;
    }

    /**
     * @return Whether this direction is from client to server
     */
    public boolean isServerbound() {
        return this == PLAY_SERVERBOUND || this == LOGIN_SERVERBOUND;
    }
}
