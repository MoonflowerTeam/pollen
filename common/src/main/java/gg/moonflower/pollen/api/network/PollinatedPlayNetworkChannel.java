package gg.moonflower.pollen.api.network;

import gg.moonflower.pollen.api.network.message.PollinatedPacket;
import gg.moonflower.pollen.api.network.message.PollinatedPacketDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedPlayNetworkChannel {

    void sendTo(ServerPlayer player, PollinatedPacket<?> message);

    void sendTo(ServerLevel level, PollinatedPacket<?> message);

    void sendToNear(ServerLevel level, double x, double y, double z, double radius, PollinatedPacket<?> message);

    void sendToAll(MinecraftServer server, PollinatedPacket<?> message);

    void sendToServer(PollinatedPacket<?> message);

    void sendToTracking(Entity entity, PollinatedPacket<?> message);

    void sendToTracking(ServerLevel level, BlockPos pos, PollinatedPacket<?> message);

    void sendToTracking(ServerLevel level, ChunkPos pos, PollinatedPacket<?> message);

    void sendToTrackingAndSelf(Entity entity, PollinatedPacket<?> message);

    /**
     * Registers a message intended to be sent during the play network phase.
     *
     * @param clazz        The class of the message
     * @param deserializer The generator for a new message
     * @param direction    The direction the message should be able to go or null for bi-directional
     * @param <MSG>        The type of message to be sent
     * @param <T>          The handler that will process the message. Should be an interface to avoid loading client classes on server
     */
    <MSG extends PollinatedPacket<T>, T> void register(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, @Nullable PollinatedPacketDirection direction);
}
