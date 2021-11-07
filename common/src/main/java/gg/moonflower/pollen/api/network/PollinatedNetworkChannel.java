package gg.moonflower.pollen.api.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.network.message.PollinatedPacketContext;
import gg.moonflower.pollen.api.network.message.PollinatedPacket;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ServerStatusPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * <p>Manages the registering of network messages between the client and server.</p>
 *
 * @author Ocelot
 * @since 3.2.0
 */
public interface PollinatedNetworkChannel {

    /**
     * Creates a new network channel with the specified id and client/server packet handlers that follows the play protocol.
     *
     * @param channelId     The id of the channel
     * @param clientFactory The factory to create a new client packet handler
     * @param serverFactory The factory to create a new server packet handler
     * @return A multi-platform network channel
     */
    @ExpectPlatform
    static PollinatedPlayNetworkChannel createPlay(ResourceLocation channelId, String version, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory) {
        throw new AssertionError();
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
    static PollinatedLoginNetworkChannel createLogin(ResourceLocation channelId, String version, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory) {
        throw new AssertionError();
    }

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    static <MSG extends PollinatedPacket<T>, T> void processMessage(@NotNull MSG msg, PollinatedPacketContext context, Object handler) {
        try {
            msg.processPacket((T) handler, context);
        } catch (Exception e) {
            LogManager.getLogger().error("Failed to process packet for class: " + msg.getClass().getName(), e);

            Component reason = new TranslatableComponent("disconnect.genericReason", "Internal Exception: " + e);
            Connection networkManager = context.getNetworkManager();
            PacketListener netHandler = networkManager.getPacketListener();

            // Need to check the channel type to determine how to disconnect
            if (netHandler instanceof ServerStatusPacketListener)
                networkManager.disconnect(reason);
            if (netHandler instanceof ServerLoginPacketListenerImpl)
                ((ServerLoginPacketListenerImpl) netHandler).disconnect(reason);
            if (netHandler instanceof ServerGamePacketListenerImpl)
                ((ServerGamePacketListenerImpl) netHandler).disconnect(reason);
            if (netHandler instanceof ClientStatusPacketListener) {
                networkManager.disconnect(reason);
                netHandler.onDisconnect(reason);
            }
            if (netHandler instanceof ClientLoginPacketListener) {
                networkManager.disconnect(reason);
                netHandler.onDisconnect(reason);
            }
        }
    }
}
