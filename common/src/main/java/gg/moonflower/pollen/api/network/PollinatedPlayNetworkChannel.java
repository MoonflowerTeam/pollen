package gg.moonflower.pollen.api.network;

import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

/**
 * Manages the registering packets between the client and server during normal gameplay.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedPlayNetworkChannel {

    /**
     * Sends the specified packet to the specified player.
     *
     * @param player The player to receive the packet
     * @param packet The packet to send
     */
    void sendTo(ServerPlayer player, PollinatedPacket<?> packet);

    /**
     * Sends the all players in the specified level.
     *
     * @param level  The level to broadcast the packet to
     * @param packet The packet to send
     */
    void sendTo(ServerLevel level, PollinatedPacket<?> packet);

    /**
     * Sends the all players near the specified spot in the specific level.
     *
     * @param level  The level to broadcast the packet to
     * @param x      The x position to target
     * @param y      The y position to target
     * @param z      The z position to target
     * @param radius The circular radius to send the packet in
     * @param packet The packet to send
     */
    void sendToNear(ServerLevel level, double x, double y, double z, double radius, PollinatedPacket<?> packet);

    /**
     * Sends the all players in the server.
     *
     * @param server The minecraft server instance
     * @param packet The packet to send
     */
    void sendToAll(MinecraftServer server, PollinatedPacket<?> packet);

    /**
     * Sends the all players tracking the specified entity.
     *
     * @param entity The entity to get listening players from
     * @param packet The packet to send
     */
    void sendToTracking(Entity entity, PollinatedPacket<?> packet);

    /**
     * Sends the all players tracking the specified position in the level.
     *
     * @param level  The level to broadcast the packet to
     * @param pos    The pos to get listening players from
     * @param packet The packet to send
     */
    void sendToTracking(ServerLevel level, BlockPos pos, PollinatedPacket<?> packet);

    /**
     * Sends the all players tracking the specified chunk in the level.
     *
     * @param chunk  The chunk to track
     * @param packet The packet to send
     */
    default void sendToTracking(LevelChunk chunk, PollinatedPacket<?> packet) {
        this.sendToTracking((ServerLevel) chunk.getLevel(), chunk.getPos(), packet);
    }

    /**
     * Sends the all players tracking the specified chunk in the level.
     *
     * @param level  The level to broadcast the packet to
     * @param pos    The pos to get listening players from
     * @param packet The packet to send
     */
    void sendToTracking(ServerLevel level, ChunkPos pos, PollinatedPacket<?> packet);

    /**
     * Sends the all players tracking the specified entity and the entity itself.
     *
     * @param entity The entity to get listening players from
     * @param packet The packet to send
     */
    void sendToTrackingAndSelf(Entity entity, PollinatedPacket<?> packet);

    /**
     * Sends a packet to the server from the client.
     *
     * @param packet THe packet to send
     */
    void sendToServer(PollinatedPacket<?> packet);

    /**
     * Registers a packet intended to be sent during the play network phase.
     *
     * @param clazz        The class of the packet
     * @param deserializer The generator for a new packet
     * @param direction    The direction the packet should be able to go or null for bidirectional
     * @param <MSG>        The type of packet to be sent
     * @param <T>          The handler that will process the packet. Should be an interface to avoid loading client classes on server
     */
    <MSG extends PollinatedPacket<T>, T> void register(Class<MSG> clazz, PacketDeserializer<MSG, T> deserializer, @Nullable PollinatedPacketDirection direction);

    /**
     * Translates the specified packet into a vanilla packet.
     *
     * @param packet    The packet to send
     * @param direction The direction to send it in
     * @return A new vanilla packet
     */
    Packet<?> toVanillaPacket(PollinatedPacket<?> packet, PollinatedPacketDirection direction);
}
