package gg.moonflower.pollen.impl.registry.network.forge;

import gg.moonflower.pollen.api.network.v1.PacketDeserializer;
import gg.moonflower.pollen.api.network.v1.PollinatedNetworkChannel;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.impl.registry.network.PollinatedNetworkRegistryImpl;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@ApiStatus.Internal
public class PollinatedNetworkChannelImpl implements PollinatedNetworkChannel {

    protected final SimpleChannel channel;
    protected Object clientMessageHandler;
    protected Object serverMessageHandler;
    protected int nextId;

    protected PollinatedNetworkChannelImpl(SimpleChannel channel) {
        this.channel = channel;
    }

    protected static NetworkDirection toNetworkDirection(@Nullable PollinatedPacketDirection direction) {
        if (direction == null)
            return null;
        return switch (direction) {
            case PLAY_SERVERBOUND -> NetworkDirection.PLAY_TO_SERVER;
            case PLAY_CLIENTBOUND -> NetworkDirection.PLAY_TO_CLIENT;
            case LOGIN_SERVERBOUND -> NetworkDirection.LOGIN_TO_SERVER;
            case LOGIN_CLIENTBOUND -> NetworkDirection.LOGIN_TO_CLIENT;
        };
    }

    protected <MSG extends PollinatedPacket<T>, T> SimpleChannel.MessageBuilder<MSG> getMessageBuilder(Class<MSG> clazz, PacketDeserializer<MSG, T> deserializer, @Nullable PollinatedPacketDirection direction) {
        return this.channel.messageBuilder(clazz, this.nextId++, toNetworkDirection(direction)).encoder((msg, buf) -> {
            try {
                msg.writePacketData(buf);
            } catch (IOException e) {
                throw new EncoderException("Failed to write packet data", e);
            }
        }).decoder(buf -> {
            try {
                return deserializer.deserialize(buf);
            } catch (IOException e) {
                throw new DecoderException("Failed to read packet data", e);
            }
        }).consumerNetworkThread((msg, ctx) ->
        {
            PollinatedNetworkRegistryImpl.processMessage(msg, new PollinatedForgePacketContext(this.channel, ctx), ctx.get().getDirection().getReceptionSide().isClient() ? this.clientMessageHandler : this.serverMessageHandler);
            ctx.get().setPacketHandled(true);
        });
    }

    @Override
    public void setClientHandler(@Nullable Object handler) {
        this.clientMessageHandler = handler;
    }

    @Override
    public void setServerHandler(@Nullable Object handler) {
        this.serverMessageHandler = handler;
    }
}
