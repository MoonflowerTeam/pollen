package gg.moonflower.pollen.api.network.forge;

import gg.moonflower.pollen.api.network.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.PollinatedNetworkChannel;
import gg.moonflower.pollen.api.network.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.api.network.message.PollinatedPacket;
import gg.moonflower.pollen.api.network.message.PollinatedPacketDirection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedNetworkChannelImpl implements PollinatedNetworkChannel {

    protected final SimpleChannel channel;
    protected final LazyLoadedValue<LazyLoadedValue<Object>> clientMessageHandler;
    protected final LazyLoadedValue<LazyLoadedValue<Object>> serverMessageHandler;
    protected int nextId;

    protected PollinatedNetworkChannelImpl(SimpleChannel channel, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory) {
        this.channel = channel;
        this.clientMessageHandler = new LazyLoadedValue<>(() -> new LazyLoadedValue<>(clientFactory.get()));
        this.serverMessageHandler = new LazyLoadedValue<>(() -> new LazyLoadedValue<>(serverFactory.get()));
    }

    protected static NetworkDirection toNetworkDirection(@Nullable PollinatedPacketDirection direction) {
        if (direction == null)
            return null;
        switch (direction) {
            case PLAY_SERVERBOUND:
                return NetworkDirection.PLAY_TO_SERVER;
            case PLAY_CLIENTBOUND:
                return NetworkDirection.PLAY_TO_CLIENT;
            case LOGIN_SERVERBOUND:
                return NetworkDirection.LOGIN_TO_SERVER;
            case LOGIN_CLIENTBOUND:
                return NetworkDirection.LOGIN_TO_CLIENT;
            default:
                throw new IllegalStateException("Unknown network direction: " + direction);
        }
    }

    public static PollinatedPlayNetworkChannel createPlay(ResourceLocation channelId, String version, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory) {
        return new PollinatedForgePlayChannel(NetworkRegistry.newSimpleChannel(channelId, () -> version, version::equals, version::equals), clientFactory, serverFactory);
    }

    public static PollinatedLoginNetworkChannel createLogin(ResourceLocation channelId, String version, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory) {
        return new PollinatedForgeLoginChannel(NetworkRegistry.newSimpleChannel(channelId, () -> version, version::equals, version::equals), clientFactory, serverFactory);
    }

    protected <MSG extends PollinatedPacket<T>, T> SimpleChannel.MessageBuilder<MSG> getMessageBuilder(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, @Nullable PollinatedPacketDirection direction) {
        return this.channel.messageBuilder(clazz, this.nextId++, toNetworkDirection(direction)).encoder(PollinatedPacket::writePacketData).decoder(deserializer).consumer((msg, ctx) ->
        {
            PollinatedNetworkChannel.processMessage(msg, new PollinatedForgePacketContext(this.channel, ctx), ctx.get().getDirection().getReceptionSide().isClient() ? this.clientMessageHandler.get().get() : this.serverMessageHandler.get().get());
            ctx.get().setPacketHandled(true);
        });
    }
}
