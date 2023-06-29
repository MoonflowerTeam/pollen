package gg.moonflower.pollen.impl.registry.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import gg.moonflower.pollen.api.network.v1.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.v1.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacketContext;
import gg.moonflower.pollen.core.Pollen;
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

@ApiStatus.Internal
public final class PollinatedNetworkRegistryImpl {

    @ExpectPlatform
    public static PollinatedPlayNetworkChannel createPlay(ResourceLocation channelId, String version) {
        return Pollen.expect();
    }

    public static PollinatedPlayNetworkChannel createPlay(ResourceLocation channelId, String version, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        PollinatedPlayNetworkChannel channel = createPlay(channelId, version);
        if (Platform.getEnvironment() == Env.CLIENT) {
            channel.setClientHandler(clientFactory.get());
        }
        channel.setServerHandler(serverFactory.get());
        return channel;
    }

    @ExpectPlatform
    public static PollinatedLoginNetworkChannel createLogin(ResourceLocation channelId, String version) {
        return Pollen.expect();
    }

    public static PollinatedLoginNetworkChannel createLogin(ResourceLocation channelId, String version, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        PollinatedLoginNetworkChannel channel = createLogin(channelId, version);
        if (Platform.getEnvironment() == Env.CLIENT) {
            channel.setClientHandler(clientFactory.get());
        }
        channel.setServerHandler(serverFactory.get());
        return channel;
    }

    @SuppressWarnings("unchecked")
    public static <MSG extends PollinatedPacket<T>, T> void processMessage(@NotNull MSG msg, PollinatedPacketContext context, Object handler) {
        try {
            msg.processPacket((T) handler, context);
        } catch (Throwable t) {
            LogManager.getLogger().error("Failed to process packet for class: " + msg.getClass().getName(), t);

            Component reason = Component.translatable("disconnect.genericReason", "Internal Exception: " + t);
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
                context.enqueueWork(() -> netHandler.onDisconnect(reason));
            }
        }
    }
}
