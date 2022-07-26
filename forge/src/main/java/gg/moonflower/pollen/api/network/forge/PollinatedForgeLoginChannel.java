package gg.moonflower.pollen.api.network.forge;

import gg.moonflower.pollen.api.network.PacketDeserializer;
import gg.moonflower.pollen.api.network.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.api.network.packet.login.PollinatedLoginPacket;
import gg.moonflower.pollen.api.registry.NetworkRegistry;
import net.minecraft.network.protocol.Packet;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedForgeLoginChannel extends PollinatedNetworkChannelImpl implements PollinatedLoginNetworkChannel {

    public PollinatedForgeLoginChannel(SimpleChannel channel, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        super(channel, clientFactory, serverFactory);
    }

    @Override
    public <MSG extends PollinatedLoginPacket<T>, T> void register(Class<MSG> clazz, PacketDeserializer<MSG, T> deserializer) {
        this.channel.messageBuilder(clazz, this.nextId++, NetworkDirection.LOGIN_TO_SERVER)
                .encoder((msg, buf) -> {
                    try {
                        msg.writePacketData(buf);
                    } catch (IOException e) {
                        throw new IllegalStateException("Failed to write packet data", e);
                    }
                })
                .decoder(buf -> {
                    try {
                        return deserializer.deserialize(buf);
                    } catch (IOException e) {
                        throw new IllegalStateException("Failed to read packet data", e);
                    }
                })
                .consumerMainThread(HandshakeHandler.indexFirst((__, msg, ctx) ->
                {
                    NetworkRegistry.processMessage(msg, new PollinatedForgePacketContext(this.channel, ctx), ctx.get().getDirection().getReceptionSide().isClient() ? this.clientMessageHandler : this.serverMessageHandler);
                }))
                .loginIndex(PollinatedLoginPacket::getAsInt, PollinatedLoginPacket::setLoginIndex)
                .add();
    }

    @Override
    public <MSG extends PollinatedLoginPacket<T>, T> void registerLogin(Class<MSG> clazz, PacketDeserializer<MSG, T> deserializer, Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators) {
        this.getMessageBuilder(clazz, deserializer, PollinatedPacketDirection.LOGIN_CLIENTBOUND)
                .loginIndex(PollinatedLoginPacket::getAsInt, PollinatedLoginPacket::setLoginIndex)
                .buildLoginPacketList(loginPacketGenerators)
                .add();
    }

    @Override
    public Packet<?> toVanillaPacket(PollinatedPacket<?> packet, int transactionId, PollinatedPacketDirection direction) {
        return this.channel.toVanillaPacket(packet, toNetworkDirection(direction));
    }
}
