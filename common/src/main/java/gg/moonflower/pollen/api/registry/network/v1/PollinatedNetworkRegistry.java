package gg.moonflower.pollen.api.registry.network.v1;

import gg.moonflower.pollen.api.network.v1.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.v1.PollinatedNetworkChannel;
import gg.moonflower.pollen.api.network.v1.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.impl.registry.network.PollinatedNetworkRegistryImpl;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Creates network channels for handling packets between the client and server.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface PollinatedNetworkRegistry {

    /**
     * <p>Creates a new network channel with the specified id and client/server packet handlers that follows the play protocol.</p>
     * <p>The handlers must be set independently with {@link PollinatedNetworkChannel#setClientHandler(Object)} and {@link PollinatedNetworkChannel#setServerHandler(Object)}</p>
     *
     * @param channelId The id of the channel
     * @param version   An arbitrary string to figure out if packets are compatible between versions
     * @return A multi-platform network channel
     */
    static PollinatedPlayNetworkChannel createPlay(ResourceLocation channelId, String version) {
        return PollinatedNetworkRegistryImpl.createPlay(channelId, version);
    }

    /**
     * <p>Creates a new network channel with the specified id and client/server packet handlers that follows the play protocol.</p>
     *
     * @param channelId     The id of the channel
     * @param clientFactory The factory to create a new client packet handler
     * @param serverFactory The factory to create a new server packet handler
     * @param version       An arbitrary string to figure out if packets are compatible between versions
     * @return A multi-platform network channel
     */
    static PollinatedPlayNetworkChannel createPlay(ResourceLocation channelId, String version, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        return PollinatedNetworkRegistryImpl.createPlay(channelId, version, clientFactory, serverFactory);
    }

    /**
     * <p>Creates a new network channel with the specified id and client/server packet handlers that follows the login protocol.</p>
     * <p>The handlers must be set independently with {@link PollinatedNetworkChannel#setClientHandler(Object)} and {@link PollinatedNetworkChannel#setServerHandler(Object)}</p>
     *
     * @param channelId The id of the channel
     * @param version   An arbitrary string to figure out if packets are compatible between versions
     * @return A multi-platform network channel
     */
    static PollinatedLoginNetworkChannel createLogin(ResourceLocation channelId, String version) {
        return PollinatedNetworkRegistryImpl.createLogin(channelId, version);
    }

    /**
     * <p>Creates a new network channel with the specified id and client/server packet handlers that follows the login protocol.</p>
     *
     * @param channelId     The id of the channel
     * @param clientFactory The factory to create a new client packet handler
     * @param serverFactory The factory to create a new server packet handler
     * @param version       An arbitrary string to figure out if packets are compatible between versions
     * @return A multi-platform network channel
     */
    static PollinatedLoginNetworkChannel createLogin(ResourceLocation channelId, String version, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        return PollinatedNetworkRegistryImpl.createLogin(channelId, version, clientFactory, serverFactory);
    }
}
