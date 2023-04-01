package gg.moonflower.pollen.api.network.v1;

import org.jetbrains.annotations.Nullable;

/**
 * Manages packets sent between the client and server and how each side handles them.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface PollinatedNetworkChannel {

    /**
     * Sets the interface that will handle all packets coming from the server to the client.
     * If there is no handler when a packet is received or the handler is not compatible, the connection will be terminated.
     *
     * @param handler The receiver for packets from the server
     */
    void setClientHandler(@Nullable Object handler);

    /**
     * Sets the interface that will handle all packets coming from the client to the server.
     * If there is no handler when a packet is received or the handler is not compatible, the connection will be terminated.
     *
     * @param handler The receiver for packets from the client
     */
    void setServerHandler(@Nullable Object handler);
}
