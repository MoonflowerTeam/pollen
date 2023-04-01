package gg.moonflower.pollen.impl.network;

import gg.moonflower.pollen.api.network.v1.PacketDeserializer;
import gg.moonflower.pollen.api.network.v1.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.impl.network.context.PollinatedFabricPlayPacketContext;
import gg.moonflower.pollen.impl.registry.network.PollinatedNetworkRegistryImpl;
import gg.moonflower.pollen.impl.registry.network.fabric.PollinatedNetworkRegistryImplImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class PollinatedFabricPlayChannel extends PollinatedNetworkChannelImpl implements PollinatedPlayNetworkChannel {

    public PollinatedFabricPlayChannel(ResourceLocation channelId) {
        super(channelId);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            PollinatedNetworkRegistryImplImpl.WRAPPER.registerPlay(this.channelId, this::processClientPlay);
//            ClientPlayNetworking.registerGlobalReceiver(this.channelId, this::processClientPlay);
        }
        ServerPlayNetworking.registerGlobalReceiver(this.channelId, this::processServerPlay);
    }

    private void processClientPlay(PacketListener listener, FriendlyByteBuf data, PacketSender responseSender) {
        PollinatedNetworkRegistryImpl.processMessage(this.deserialize(data, PollinatedPacketDirection.PLAY_CLIENTBOUND), new PollinatedFabricPlayPacketContext(listener.getConnection(), pkt -> responseSender.sendPacket(responseSender.createPacket(this.channelId, this.serialize(pkt, PollinatedPacketDirection.PLAY_SERVERBOUND))), PollinatedPacketDirection.PLAY_CLIENTBOUND), this.clientMessageHandler);
    }

    private void processServerPlay(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, FriendlyByteBuf data, PacketSender responseSender) {
        PollinatedNetworkRegistryImpl.processMessage(this.deserialize(data, PollinatedPacketDirection.PLAY_SERVERBOUND), new PollinatedFabricPlayPacketContext(listener.getConnection(), pkt -> responseSender.sendPacket(responseSender.createPacket(this.channelId, this.serialize(pkt, PollinatedPacketDirection.PLAY_CLIENTBOUND))), PollinatedPacketDirection.PLAY_SERVERBOUND), this.serverMessageHandler);
    }

    @Override
    public void sendTo(ServerPlayer player, PollinatedPacket<?> packet) {
        ServerPlayNetworking.send(player, this.channelId, this.serialize(packet, PollinatedPacketDirection.PLAY_CLIENTBOUND));
    }

    @Override
    public void sendTo(ServerLevel level, PollinatedPacket<?> packet) {
        FriendlyByteBuf data = this.serialize(packet, PollinatedPacketDirection.PLAY_CLIENTBOUND);
        for (ServerPlayer player : PlayerLookup.world(level)) {
            ServerPlayNetworking.send(player, this.channelId, data);
        }
    }

    @Override
    public void sendToNear(ServerLevel level, double x, double y, double z, double radius, PollinatedPacket<?> packet) {
        FriendlyByteBuf data = this.serialize(packet, PollinatedPacketDirection.PLAY_CLIENTBOUND);
        for (ServerPlayer player : PlayerLookup.around(level, new Vec3(x, y, z), radius)) {
            ServerPlayNetworking.send(player, this.channelId, data);
        }
    }

    @Override
    public void sendToAll(MinecraftServer server, PollinatedPacket<?> packet) {
        FriendlyByteBuf data = this.serialize(packet, PollinatedPacketDirection.PLAY_CLIENTBOUND);
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, this.channelId, data);
        }
    }

    @Override
    public void sendToTracking(Entity entity, PollinatedPacket<?> packet) {
        FriendlyByteBuf data = this.serialize(packet, PollinatedPacketDirection.PLAY_CLIENTBOUND);
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, this.channelId, data);
        }
    }

    @Override
    public void sendToTracking(ServerLevel level, BlockPos pos, PollinatedPacket<?> packet) {
        FriendlyByteBuf data = this.serialize(packet, PollinatedPacketDirection.PLAY_CLIENTBOUND);
        for (ServerPlayer player : PlayerLookup.tracking(level, pos)) {
            ServerPlayNetworking.send(player, this.channelId, data);
        }
    }

    @Override
    public void sendToTracking(ServerLevel level, ChunkPos pos, PollinatedPacket<?> packet) {
        FriendlyByteBuf data = this.serialize(packet, PollinatedPacketDirection.PLAY_CLIENTBOUND);
        for (ServerPlayer player : PlayerLookup.tracking(level, pos)) {
            ServerPlayNetworking.send(player, this.channelId, data);
        }
    }

    @Override
    public void sendToTrackingAndSelf(Entity entity, PollinatedPacket<?> packet) {
        FriendlyByteBuf data = this.serialize(packet, PollinatedPacketDirection.PLAY_CLIENTBOUND);
        if (entity instanceof ServerPlayer player) {
            ServerPlayNetworking.send(player, this.channelId, data);
        }
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, this.channelId, data);
        }
    }

    @Override
    public void sendToServer(PollinatedPacket<?> packet) {
        ClientPlayNetworking.send(this.channelId, this.serialize(packet, PollinatedPacketDirection.PLAY_SERVERBOUND));
    }

    @Override
    public Packet<?> toVanillaPacket(PollinatedPacket<?> packet, PollinatedPacketDirection direction) {
        return switch (direction) {
            case PLAY_SERVERBOUND ->
                    new ServerboundCustomPayloadPacket(this.channelId, this.serialize(packet, direction));
            case PLAY_CLIENTBOUND ->
                    new ClientboundCustomPayloadPacket(this.channelId, this.serialize(packet, direction));
            default -> throw new IllegalStateException("Unsupported packet direction: " + direction);
        };
    }

    @Override
    public <MSG extends PollinatedPacket<T>, T> void register(Class<MSG> clazz, PacketDeserializer<MSG, T> deserializer, @Nullable PollinatedPacketDirection direction) {
        super.register(clazz, deserializer, direction);
    }
}
