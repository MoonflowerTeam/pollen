package gg.moonflower.pollen.api.registry.network.v1;

import gg.moonflower.pollen.api.network.v1.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.v1.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.impl.registry.network.PollinatedNetworkRegistryImpl;
import net.minecraft.resources.ResourceLocation;

/**
 * Creates network channels for handling packets between the client and server.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface PollinatedNetworkRegistry {

    /**
     * Creates a new network channel with the specified id and client/server packet handlers that follows the play protocol.
     *
     * @param channelId The id of the channel
     * @param version   An arbitrary string to figure out if packets are compatible between versions
     * @return A multi-platform network channel
     */
    static PollinatedPlayNetworkChannel createPlay(ResourceLocation channelId, String version) {
        return PollinatedNetworkRegistryImpl.createPlay(channelId, version);
    }

    /**
     * Creates a new network channel with the specified id and client/server packet handlers that follows the login protocol.
     *
     * @param channelId The id of the channel
     * @param version   An arbitrary string to figure out if packets are compatible between versions
     * @return A multi-platform network channel
     */
    static PollinatedLoginNetworkChannel createLogin(ResourceLocation channelId, String version) {
        return PollinatedNetworkRegistryImpl.createLogin(channelId, version);
    }
}
