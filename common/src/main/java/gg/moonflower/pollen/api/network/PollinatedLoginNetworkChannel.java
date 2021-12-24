package gg.moonflower.pollen.api.network;

import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.api.network.packet.login.PollinatedLoginPacket;
import net.minecraft.network.protocol.Packet;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Manages the registering packets between the client and server during the login phase.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedLoginNetworkChannel {

    /**
     * Registers a packet intended to be sent during the login network phase. These packets should be intended for a server audience.
     *
     * @param clazz        The class of the packet
     * @param deserializer The generator for a new packet
     * @param <MSG>        The type of packet to be sent
     * @param <T>          The handler that will process the packet. Should be an interface to avoid loading client classes on server
     */
    <MSG extends PollinatedLoginPacket<T>, T> void register(Class<MSG> clazz, PacketDeserializer<MSG, T> deserializer);

    /**
     * Registers a packet intended to be sent during the login network phase. These packets should be intended for a client audience.
     *
     * @param clazz                The class of the packet
     * @param deserializer         The generator for a new packet
     * @param loginPacketGenerator The function to generate a login packet
     * @param <MSG>                The type of packet to be sent
     * @param <T>                  The handler that will process the packet. Should be an interface to avoid loading client classes on server
     */
    default <MSG extends PollinatedLoginPacket<T>, T> void registerLogin(Class<MSG> clazz, PacketDeserializer<MSG, T> deserializer, Supplier<MSG> loginPacketGenerator) {
        this.registerLogin(clazz, deserializer, localChannel -> Collections.singletonList(Pair.of(clazz.getSimpleName(), loginPacketGenerator.get())));
    }

    /**
     * Registers a packet intended to be sent during the login network phase. Allows the custom definition of login packets. These packets should be intended for a client audience.
     *
     * @param clazz                 The class of the packet
     * @param deserializer          The generator for a new packet
     * @param loginPacketGenerators The function to generate login packets
     * @param <MSG>                 The type of packet to be sent
     * @param <T>                   The handler that will process the packet. Should be an interface to avoid loading client classes on server
     */
    <MSG extends PollinatedLoginPacket<T>, T> void registerLogin(Class<MSG> clazz, PacketDeserializer<MSG, T> deserializer, Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators);

    /**
     * Translates the specified packet into a vanilla packet.
     *
     * @param transactionId The id of the current transaction
     * @param packet        The packet to send
     * @param direction     The direction to send it in
     * @return A new vanilla packet
     */
    Packet<?> toVanillaPacket(PollinatedPacket<?> packet, int transactionId, PollinatedPacketDirection direction);
}
