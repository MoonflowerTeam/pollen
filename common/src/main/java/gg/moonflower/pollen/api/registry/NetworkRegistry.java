package gg.moonflower.pollen.api.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.network.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Manages the registering of network channels between the client and server.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class NetworkRegistry {

    private NetworkRegistry() {
    }

    /**
     * Creates a new network channel with the specified id and client/server packet handlers that follows the play protocol.
     *
     * @param channelId     The id of the channel
     * @param clientFactory The factory to create a new client packet handler
     * @param serverFactory The factory to create a new server packet handler
     * @return A multi-platform network channel
     */
    @ExpectPlatform
    public static PollinatedPlayNetworkChannel createPlay(ResourceLocation channelId, String version, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        return Platform.error();
    }

    /**
     * Creates a new network channel with the specified id and client/server packet handlers that follows the login protocol.
     *
     * @param channelId     The id of the channel
     * @param clientFactory The factory to create a new client packet handler
     * @param serverFactory The factory to create a new server packet handler
     * @return A multi-platform network channel
     */
    @ExpectPlatform
    public static PollinatedLoginNetworkChannel createLogin(ResourceLocation channelId, String version, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        return Platform.error();
    }

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public static <MSG extends PollinatedPacket<T>, T> void processMessage(@NotNull MSG msg, PollinatedPacketContext context, Supplier<Object> handler) {
        try {
            msg.processPacket((T) handler.get(), context);
        } catch (Exception e) {
            LogManager.getLogger().error("Failed to process packet for class: " + msg.getClass().getName(), e);

            Component reason = Component.translatable("disconnect.genericReason", "Internal Exception: " + e);
            Connection networkManager = context.getNetworkManager();
            PacketListener netHandler = networkManager.getPacketListener();

            // Need to check the channel type to determine how to disconnect
            if (netHandler instanceof ServerStatusPacketListener) {
                networkManager.disconnect(reason);
            } else if (netHandler instanceof ServerLoginPacketListenerImpl) {
                ((ServerLoginPacketListenerImpl) netHandler).disconnect(reason);
            } else if (netHandler instanceof ServerGamePacketListenerImpl) {
                ((ServerGamePacketListenerImpl) netHandler).disconnect(reason);
            } else {
                networkManager.disconnect(reason);
                netHandler.onDisconnect(reason);
            }
        }
    }
}
