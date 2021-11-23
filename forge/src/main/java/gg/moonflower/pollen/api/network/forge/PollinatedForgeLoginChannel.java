package gg.moonflower.pollen.api.network.forge;

import gg.moonflower.pollen.api.network.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.api.network.packet.login.PollinatedLoginPacket;
import gg.moonflower.pollen.api.registry.NetworkRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.FMLHandshakeHandler;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedForgeLoginChannel extends PollinatedNetworkChannelImpl implements PollinatedLoginNetworkChannel {

    public PollinatedForgeLoginChannel(SimpleChannel channel, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        super(channel, clientFactory, serverFactory);
    }

    @Override
    public <MSG extends PollinatedLoginPacket<T>, T> void register(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer) {
        this.channel.messageBuilder(clazz, this.nextId++, NetworkDirection.LOGIN_TO_SERVER).encoder(PollinatedPacket::writePacketData).decoder(deserializer)
                .consumer(FMLHandshakeHandler.indexFirst((__, msg, ctx) ->
                {
                    NetworkRegistry.processMessage(msg, new PollinatedForgePacketContext(this.channel, ctx), ctx.get().getDirection().getReceptionSide().isClient() ? this.clientMessageHandler : this.serverMessageHandler);
                    ctx.get().setPacketHandled(true);
                }))
                .loginIndex(PollinatedLoginPacket::getAsInt, PollinatedLoginPacket::setLoginIndex)
                .add();
    }

    @Override
    public <MSG extends PollinatedLoginPacket<T>, T> void registerLogin(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators) {
        this.getMessageBuilder(clazz, deserializer, PollinatedPacketDirection.LOGIN_CLIENTBOUND)
                .loginIndex(PollinatedLoginPacket::getAsInt, PollinatedLoginPacket::setLoginIndex)
                .buildLoginPacketList(loginPacketGenerators)
                .add();
    }
}
