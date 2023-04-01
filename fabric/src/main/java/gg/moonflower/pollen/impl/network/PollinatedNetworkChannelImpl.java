package gg.moonflower.pollen.impl.network;

import gg.moonflower.pollen.api.network.v1.PacketDeserializer;
import gg.moonflower.pollen.api.network.v1.PollinatedNetworkChannel;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacketDirection;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApiStatus.Internal
public class PollinatedNetworkChannelImpl implements PollinatedNetworkChannel {

    protected final ResourceLocation channelId;
    protected final List<PacketFactory<?, ?>> factories;
    protected Object clientMessageHandler;
    protected Object serverMessageHandler;

    protected PollinatedNetworkChannelImpl(ResourceLocation channelId) {
        this.channelId = channelId;
        this.factories = new ArrayList<>();
    }

    protected FriendlyByteBuf serialize(PollinatedPacket<?> message, PollinatedPacketDirection expectedDirection) {
        Optional<PacketFactory<?, ?>> factoryOptional = this.factories.stream().filter(factory -> factory.clazz == message.getClass()).findFirst();
        if (factoryOptional.isEmpty())
            throw new IllegalStateException("Unregistered packet: " + message.getClass() + " on channel: " + this.channelId);

        int id = this.factories.indexOf(factoryOptional.get());
        if (factoryOptional.get().direction != null && factoryOptional.get().direction != expectedDirection)
            throw new IllegalStateException("Attempted to send packet with id: " + id + ". Expected " + expectedDirection + ", got " + factoryOptional.get().direction);

        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(id);
        try {
            message.writePacketData(buf);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write packet data", e);
        }
        return buf;
    }

    protected PollinatedPacket<?> deserialize(FriendlyByteBuf buf, PollinatedPacketDirection expectedDirection) {
        int id = buf.readVarInt();
        if (id < 0 || id >= this.factories.size())
            throw new IllegalStateException("Unknown packet with id: " + id);

        PacketFactory<?, ?> factory = this.factories.get(id);
        if (factory.direction != null && factory.direction != expectedDirection)
            throw new IllegalStateException("Received unexpected packet with id: " + id + ". Expected " + expectedDirection + ", got " + factory.direction);

        try {
            return factory.deserializer.deserialize(buf);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read packet data", e);
        }
    }

    protected <MSG extends PollinatedPacket<T>, T> void register(Class<MSG> clazz, PacketDeserializer<MSG, T> deserializer, @Nullable PollinatedPacketDirection direction) {
        this.factories.add(new PacketFactory<>(clazz, deserializer, direction));
    }

    @Override
    public void setClientHandler(@Nullable Object handler) {
        this.clientMessageHandler = handler;
    }

    @Override
    public void setServerHandler(@Nullable Object handler) {
        this.serverMessageHandler = handler;
    }

    private record PacketFactory<MSG extends PollinatedPacket<T>, T>(Class<MSG> clazz,
                                                                     PacketDeserializer<MSG, T> deserializer,
                                                                     PollinatedPacketDirection direction) {
    }
}
