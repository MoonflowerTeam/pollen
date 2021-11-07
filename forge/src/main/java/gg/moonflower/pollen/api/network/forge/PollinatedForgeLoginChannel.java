package gg.moonflower.pollen.api.network.forge;

import gg.moonflower.pollen.api.network.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.PollinatedNetworkChannel;
import gg.moonflower.pollen.api.network.message.login.PollinatedLoginPacket;
import gg.moonflower.pollen.api.network.message.PollinatedPacket;
import gg.moonflower.pollen.api.network.message.PollinatedPacketDirection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedForgeLoginChannel extends PollinatedNetworkChannelImpl implements PollinatedLoginNetworkChannel {

    PollinatedForgeLoginChannel(SimpleChannel channel, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory) {
        super(channel, clientFactory, serverFactory);
    }

    @Override
    public <MSG extends PollinatedLoginPacket<T>, T> void registerLoginReply(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, @Nullable PollinatedPacketDirection direction) {
        this.channel.messageBuilder(clazz, this.nextId++, toNetworkDirection(direction)).encoder(PollinatedPacket::writePacketData).decoder(deserializer)
                .consumer(FMLHandshakeHandler.indexFirst((__, msg, ctx) ->
                {
                    PollinatedNetworkChannel.processMessage(msg, new PollinatedForgePacketContext(this.channel, ctx), ctx.get().getDirection().getReceptionSide().isClient() ? this.clientMessageHandler.get().get() : this.serverMessageHandler.get().get());
                    ctx.get().setPacketHandled(true);
                }))
                .loginIndex(PollinatedLoginPacket::getAsInt, PollinatedLoginPacket::setLoginIndex)
                .add();
    }

    @Override
    public <MSG extends PollinatedLoginPacket<T>, T> void registerLogin(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators, @Nullable PollinatedPacketDirection direction) {
        this.getMessageBuilder(clazz, deserializer, direction)
                .loginIndex(PollinatedLoginPacket::getAsInt, PollinatedLoginPacket::setLoginIndex)
                .buildLoginPacketList(loginPacketGenerators)
                .add();
    }
}
