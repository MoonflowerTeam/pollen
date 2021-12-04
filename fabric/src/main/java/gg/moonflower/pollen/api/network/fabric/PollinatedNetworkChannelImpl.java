package gg.moonflower.pollen.api.network.fabric;

import com.google.common.base.Suppliers;
import gg.moonflower.pollen.api.network.PacketDeserializer;
import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedNetworkChannelImpl {

    protected final ResourceLocation channelId;
    protected final List<PacketFactory<?, ?>> factories;
    protected final Supplier<Object> clientMessageHandler;
    protected final Supplier<Object> serverMessageHandler;

    protected PollinatedNetworkChannelImpl(ResourceLocation channelId, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        this.channelId = channelId;
        this.factories = new ArrayList<>();
        this.clientMessageHandler = Suppliers.memoize(clientFactory::get);
        this.serverMessageHandler = Suppliers.memoize(serverFactory::get);
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

    private record PacketFactory<MSG extends PollinatedPacket<T>, T>(Class<MSG> clazz,
                                                                     PacketDeserializer<MSG, T> deserializer,
                                                                     PollinatedPacketDirection direction) {
    }
}
