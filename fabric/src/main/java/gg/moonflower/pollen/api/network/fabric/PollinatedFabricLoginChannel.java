package gg.moonflower.pollen.api.network.fabric;

import gg.moonflower.pollen.api.network.PacketDeserializer;
import gg.moonflower.pollen.api.network.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.fabric.context.PollinatedFabricLoginPacketContext;
import gg.moonflower.pollen.api.network.fabric.context.PollinatedFabricPacketContext;
import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.api.network.packet.login.PollinatedLoginPacket;
import gg.moonflower.pollen.api.registry.NetworkRegistry;
import gg.moonflower.pollen.core.extensions.fabric.ServerLoginPacketListenerImplExtension;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedFabricLoginChannel extends PollinatedNetworkChannelImpl implements PollinatedLoginNetworkChannel {

    private final List<Function<Boolean, ? extends List<? extends Pair<String, ?>>>> loginPackets;

    public PollinatedFabricLoginChannel(ResourceLocation channelId, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        super(channelId, clientFactory, serverFactory);
        this.loginPackets = new ArrayList<>();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientLoginNetworking.registerGlobalReceiver(this.channelId, this::processClient);
            ClientPlayNetworking.registerGlobalReceiver(this.channelId, (client, handler, buf, responseSender) -> this.processClient(client, handler, buf, null));
        }
        ServerLoginNetworking.registerGlobalReceiver(this.channelId, this::processServer);
        ServerPlayNetworking.registerGlobalReceiver(this.channelId, (server, player, handler, buf, responseSender) -> this.processServer(server, handler, true, buf, future -> {
            // Don't wait because the player is already being placed into the level
        }, responseSender));

        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> this.loginPackets.stream().flatMap(function -> function.apply(handler.getConnection().isMemoryConnection()).stream()).forEach(pair -> {
            Packet<?> packet = sender.createPacket(this.channelId, this.serialize((PollinatedPacket<?>) pair.getValue(), PollinatedPacketDirection.LOGIN_CLIENTBOUND));
            if (packet instanceof ClientboundCustomQueryPacket)
                ((ServerLoginPacketListenerImplExtension) handler).pollen_trackPacket((ClientboundCustomQueryPacket) packet);
            sender.sendPacket(packet);
        }));
    }

    private CompletableFuture<@Nullable FriendlyByteBuf> processClient(Minecraft client, PacketListener listener, FriendlyByteBuf data, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
        CompletableFuture<FriendlyByteBuf> future = new CompletableFuture<>();
        NetworkRegistry.processMessage(this.deserialize(data, PollinatedPacketDirection.LOGIN_CLIENTBOUND), new PollinatedFabricLoginPacketContext(pkt -> {
            try {
                future.complete(this.serialize(pkt, PollinatedPacketDirection.LOGIN_SERVERBOUND));
            } catch (Throwable t) {
                t.printStackTrace();
                future.completeExceptionally(t);
            }
        }, listener.getConnection(), __ -> {
        }, PollinatedPacketDirection.LOGIN_CLIENTBOUND), this.clientMessageHandler);
        return future;
    }

    private void processServer(MinecraftServer server, PacketListener listener, boolean understood, FriendlyByteBuf data, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender) {
        if (!understood) {
            if (!(listener instanceof ServerLoginPacketListenerImplExtension)) // If already being sent over the play channel
                throw new IllegalStateException("Client failed to process server message");

            // Re-send over the play channel instead
            ((ServerLoginPacketListenerImplExtension) listener).pollen_delayPacket();
            return;
        }

        NetworkRegistry.processMessage(this.deserialize(data, PollinatedPacketDirection.LOGIN_SERVERBOUND), new PollinatedFabricPacketContext(listener.getConnection(), synchronizer, PollinatedPacketDirection.LOGIN_SERVERBOUND) {
            @Override
            public void reply(PollinatedPacket<?> packet) {
                throw new UnsupportedOperationException("The server is not allowed to reply during the login phase.");
            }
        }, this.serverMessageHandler);
    }

    @Override
    public <MSG extends PollinatedLoginPacket<T>, T> void register(Class<MSG> clazz, PacketDeserializer<MSG, T> deserializer) {
        super.register(clazz, deserializer, PollinatedPacketDirection.LOGIN_SERVERBOUND);
    }

    @Override
    public <MSG extends PollinatedLoginPacket<T>, T> void registerLogin(Class<MSG> clazz, PacketDeserializer<MSG, T> deserializer, Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators) {
        super.register(clazz, deserializer, PollinatedPacketDirection.LOGIN_CLIENTBOUND);
        this.loginPackets.add(loginPacketGenerators);
    }

    @Override
    public Packet<?> toVanillaPacket(PollinatedPacket<?> packet, int transactionId, PollinatedPacketDirection direction) {
        return switch (direction) {
            case LOGIN_SERVERBOUND -> new ServerboundCustomQueryPacket(transactionId, this.serialize(packet, direction));
            case LOGIN_CLIENTBOUND -> new ClientboundCustomQueryPacket(transactionId, this.channelId, this.serialize(packet, direction));
            default -> throw new IllegalStateException("Unsupported packet direction: " + direction);
        };
    }
}
