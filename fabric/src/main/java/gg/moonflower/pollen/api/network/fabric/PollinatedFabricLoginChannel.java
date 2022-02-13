package gg.moonflower.pollen.api.network.fabric;

import gg.moonflower.pollen.api.network.PacketDeserializer;
import gg.moonflower.pollen.api.network.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.fabric.context.PollinatedFabricLoginPacketContext;
import gg.moonflower.pollen.api.network.fabric.context.PollinatedFabricPacketContext;
import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.api.network.packet.login.PollinatedLoginPacket;
import gg.moonflower.pollen.api.registry.NetworkRegistry;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.mixin.networking.accessor.LoginQueryRequestS2CPacketAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
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

    public PollinatedFabricLoginChannel(ResourceLocation channelId, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory) {
        super(channelId, clientFactory, serverFactory);
        this.loginPackets = new ArrayList<>();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            ClientLoginNetworking.registerGlobalReceiver(this.channelId, this::processClient);
        ServerLoginNetworking.registerGlobalReceiver(this.channelId, this::processServer);
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> this.loginPackets.stream().flatMap(function -> function.apply(handler.getConnection().isMemoryConnection()).stream()).forEach(pair -> sender.sendPacket(sender.createPacket(this.channelId, this.serialize((PollinatedPacket<?>) pair.getValue(), PollinatedPacketDirection.LOGIN_CLIENTBOUND)))));
    }

    private CompletableFuture<@Nullable FriendlyByteBuf> processClient(Minecraft client, ClientHandshakePacketListenerImpl listener, FriendlyByteBuf data, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
        CompletableFuture<FriendlyByteBuf> future = new CompletableFuture<>();
        NetworkRegistry.processMessage(this.deserialize(data, PollinatedPacketDirection.LOGIN_CLIENTBOUND), new PollinatedFabricLoginPacketContext(pkt -> {
            try {
                future.complete(this.serialize(pkt, PollinatedPacketDirection.LOGIN_SERVERBOUND));
            } catch (Throwable t) {
                t.printStackTrace();
                future.completeExceptionally(t);
            }
        }, listener.getConnection(), __ -> {
        }, PollinatedPacketDirection.LOGIN_CLIENTBOUND), this.clientMessageHandler.get().get());
        return future;
    }

    private void processServer(MinecraftServer server, ServerLoginPacketListenerImpl listener, boolean understood, FriendlyByteBuf data, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender) {
        if (!understood)
            throw new IllegalStateException("Client failed to process server message");
        NetworkRegistry.processMessage(this.deserialize(data, PollinatedPacketDirection.LOGIN_SERVERBOUND), new PollinatedFabricPacketContext(listener.getConnection(), synchronizer, PollinatedPacketDirection.LOGIN_SERVERBOUND) {
            @Override
            public void reply(PollinatedPacket<?> packet) {
                throw new UnsupportedOperationException("The server is not allowed to reply during the login phase.");
            }
        }, this.serverMessageHandler.get().get());
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
        switch (direction) {
            case LOGIN_SERVERBOUND:
                return new ServerboundCustomQueryPacket(transactionId, this.serialize(packet, direction));
            case LOGIN_CLIENTBOUND:
                ClientboundCustomQueryPacket value = new ClientboundCustomQueryPacket();
                LoginQueryRequestS2CPacketAccessor access = (LoginQueryRequestS2CPacketAccessor) value;
                access.setQueryId(transactionId);
                access.setChannel(this.channelId);
                access.setPayload(this.serialize(packet, direction));
                return value;
            default:
                throw new IllegalStateException("Unsupported packet direction: " + direction);
        }
    }
}
