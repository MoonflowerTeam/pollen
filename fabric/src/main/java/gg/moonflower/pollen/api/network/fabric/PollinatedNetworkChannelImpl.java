package gg.moonflower.pollen.api.network.fabric;

import gg.moonflower.pollen.api.network.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.PollinatedNetworkChannel;
import gg.moonflower.pollen.api.network.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.api.network.message.PollinatedPacket;
import gg.moonflower.pollen.api.network.message.PollinatedPacketDirection;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedNetworkChannelImpl implements PollinatedNetworkChannel {

    protected final ResourceLocation channelId;
    protected final List<PacketFactory<?, ?>> factories;
    protected final LazyLoadedValue<LazyLoadedValue<Object>> clientMessageHandler;
    protected final LazyLoadedValue<LazyLoadedValue<Object>> serverMessageHandler;

    protected PollinatedNetworkChannelImpl(ResourceLocation channelId, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory) {
        this.channelId = channelId;
        this.factories = new ArrayList<>();
        this.clientMessageHandler = new LazyLoadedValue<>(() -> new LazyLoadedValue<>(clientFactory.get()));
        this.serverMessageHandler = new LazyLoadedValue<>(() -> new LazyLoadedValue<>(serverFactory.get()));
    }

    protected FriendlyByteBuf serialize(PollinatedPacket<?> message, PollinatedPacketDirection expectedDirection) {
        Optional<PacketFactory<?, ?>> factoryOptional = this.factories.stream().filter(factory -> factory.clazz == message.getClass()).findFirst();
        if (!factoryOptional.isPresent())
            throw new IllegalStateException("Unregistered packet: " + message.getClass() + " on channel: " + this.channelId);

        int id = this.factories.indexOf(factoryOptional.get());
        if (factoryOptional.get().direction != null && factoryOptional.get().direction != expectedDirection)
            throw new IllegalStateException("Attempted to send packet with id: " + id + ". Expected " + expectedDirection + ", got " + factoryOptional.get().direction);

        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(id);
        message.writePacketData(buf);
        return buf;
    }

    protected PollinatedPacket<?> deserialize(FriendlyByteBuf buf, PollinatedPacketDirection expectedDirection) {
        int id = buf.readVarInt();
        if (id < 0 || id >= this.factories.size())
            throw new IllegalStateException("Unknown packet with id: " + id);

        PacketFactory<?, ?> factory = this.factories.get(id);
        if (factory.direction != null && factory.direction != expectedDirection)
            throw new IllegalStateException("Received unexpected packet with id: " + id + ". Expected " + expectedDirection + ", got " + factory.direction);

        return factory.deserializer.apply(buf);
    }

    protected <MSG extends PollinatedPacket<T>, T> void register(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, @Nullable PollinatedPacketDirection direction) {
        this.factories.add(new PacketFactory<>(clazz, deserializer, direction));
    }

    public static PollinatedPlayNetworkChannel createPlay(ResourceLocation channelId, String version, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory) {
        return new PollinatedFabricPlayChannel(channelId, clientFactory, serverFactory);
    }

    public static PollinatedLoginNetworkChannel createLogin(ResourceLocation channelId, String version, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory) {
        return new PollinatedFabricLoginChannel(channelId, clientFactory, serverFactory);
    }

    private static class PacketFactory<MSG extends PollinatedPacket<T>, T> {
        private final Class<MSG> clazz;
        private final Function<FriendlyByteBuf, MSG> deserializer;
        private final PollinatedPacketDirection direction;

        private PacketFactory(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, PollinatedPacketDirection direction) {
            this.clazz = clazz;
            this.deserializer = deserializer;
            this.direction = direction;
        }
    }
}
