package gg.moonflower.pollen.api.network;

import gg.moonflower.pollen.api.network.message.login.PollinatedLoginPacket;
import gg.moonflower.pollen.api.network.message.PollinatedPacketDirection;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public interface PollinatedLoginNetworkChannel {

    /**
     * Registers a message intended to be sent during the login network phase.
     *
     * @param clazz        The class of the message
     * @param deserializer The generator for a new message
     * @param direction    The direction the message should be able to go or null for bi-directional
     * @param <MSG>        The type of message to be sent
     * @param <T>          The handler that will process the message. Should be an interface to avoid loading client classes on server
     */
    <MSG extends PollinatedLoginPacket<T>, T> void registerLoginReply(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, @Nullable PollinatedPacketDirection direction);

    /**
     * Registers a message intended to be sent during the login network phase.
     *
     * @param clazz        The class of the message
     * @param deserializer The generator for a new message
     * @param direction    The direction the message should be able to go or null for bi-directional
     * @param <MSG>        The type of message to be sent
     * @param <T>          The handler that will process the message. Should be an interface to avoid loading client classes on server
     */
    default <MSG extends PollinatedLoginPacket<T>, T> void registerLogin(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, @Nullable PollinatedPacketDirection direction) {
        this.registerLogin(clazz, deserializer, localChannel -> {
            try {
                return Collections.singletonList(Pair.of(clazz.getSimpleName(), clazz.newInstance()));
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Could not access login packet constructor. Make sure it has an empty public constructor or use PollinatedLoginNetworkChannel#registerLogin with login packet generators.", e);
            }
        }, direction);
    }

    /**
     * Registers a message intended to be sent during the login network phase. Allows the custom definition of login packets.
     *
     * @param clazz                 The class of the message
     * @param deserializer          The generator for a new message
     * @param loginPacketGenerators The function to generate login packets
     * @param direction             The direction the message should be able to go or null for bi-directional
     * @param <MSG>                 The type of message to be sent
     * @param <T>                   The handler that will process the message. Should be an interface to avoid loading client classes on server
     */
    <MSG extends PollinatedLoginPacket<T>, T> void registerLogin(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators, @Nullable PollinatedPacketDirection direction);
}
