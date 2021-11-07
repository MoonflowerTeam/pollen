package gg.moonflower.pollen.api.network.fabric;

import gg.moonflower.pollen.api.network.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.PollinatedNetworkChannel;
import gg.moonflower.pollen.api.network.fabric.context.PollinatedFabricLoginPacketContext;
import gg.moonflower.pollen.api.network.fabric.context.PollinatedFabricPacketContext;
import gg.moonflower.pollen.api.network.message.PollinatedPacket;
import gg.moonflower.pollen.api.network.message.PollinatedPacketDirection;
import gg.moonflower.pollen.api.network.message.login.PollinatedLoginPacket;
import gg.moonflower.pollen.api.network.message.login.ServerboundAckPacket;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
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

    PollinatedFabricLoginChannel(ResourceLocation channelId, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory) {
        super(channelId, clientFactory, serverFactory);
        this.loginPackets = new ArrayList<>();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            ClientLoginNetworking.registerGlobalReceiver(this.channelId, this::processClient);
        ServerLoginNetworking.registerGlobalReceiver(this.channelId, this::processServer);
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> this.loginPackets.stream().flatMap(function -> function.apply(handler.getConnection().isMemoryConnection()).stream()).forEach(pair -> sender.sendPacket(sender.createPacket(this.channelId, this.serialize((PollinatedPacket<?>) pair.getValue(), PollinatedPacketDirection.LOGIN_CLIENTBOUND)))));
    }

    private CompletableFuture<@Nullable FriendlyByteBuf> processClient(Minecraft client, ClientHandshakePacketListenerImpl listener, FriendlyByteBuf data, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
        CompletableFuture<FriendlyByteBuf> future = new CompletableFuture<>();
        PollinatedNetworkChannel.processMessage(this.deserialize(data, PollinatedPacketDirection.LOGIN_CLIENTBOUND), new PollinatedFabricLoginPacketContext(pkt -> {
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
        PollinatedNetworkChannel.processMessage(this.deserialize(data, PollinatedPacketDirection.LOGIN_SERVERBOUND), new PollinatedFabricPacketContext(listener.getConnection(), synchronizer, PollinatedPacketDirection.LOGIN_SERVERBOUND) {
            @Override
            public void reply(PollinatedPacket<?> packet) {
                throw new UnsupportedOperationException("The server is not allowed to reply during the login phase.");
            }
        }, this.serverMessageHandler.get().get());
    }

    @Override
    public <MSG extends PollinatedLoginPacket<T>, T> void registerLoginReply(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, @Nullable PollinatedPacketDirection direction) {
        super.register(clazz, deserializer, direction);
    }

    @Override
    public <MSG extends PollinatedLoginPacket<T>, T> void registerLogin(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators, @Nullable PollinatedPacketDirection direction) {
        super.register(clazz, deserializer, direction);
        this.loginPackets.add(loginPacketGenerators);
    }
}
