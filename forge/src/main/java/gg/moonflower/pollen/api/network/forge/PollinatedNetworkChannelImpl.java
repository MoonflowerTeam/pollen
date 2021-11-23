package gg.moonflower.pollen.api.network.forge;

import com.google.common.base.Suppliers;
import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.api.registry.NetworkRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedNetworkChannelImpl {

    protected final SimpleChannel channel;
    protected final Supplier<Object> clientMessageHandler;
    protected final Supplier<Object> serverMessageHandler;
    protected int nextId;

    protected PollinatedNetworkChannelImpl(SimpleChannel channel, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        this.channel = channel;
        this.clientMessageHandler = Suppliers.memoize(clientFactory::get);
        this.serverMessageHandler = Suppliers.memoize(serverFactory::get);
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

    protected <MSG extends PollinatedPacket<T>, T> SimpleChannel.MessageBuilder<MSG> getMessageBuilder(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, @Nullable PollinatedPacketDirection direction) {
        return this.channel.messageBuilder(clazz, this.nextId++, toNetworkDirection(direction)).encoder(PollinatedPacket::writePacketData).decoder(deserializer).consumer((msg, ctx) ->
        {
            NetworkRegistry.processMessage(msg, new PollinatedForgePacketContext(this.channel, ctx), ctx.get().getDirection().getReceptionSide().isClient() ? this.clientMessageHandler : this.serverMessageHandler);
            ctx.get().setPacketHandled(true);
        });
    }
}
