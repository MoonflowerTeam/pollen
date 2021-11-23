package gg.moonflower.pollen.api.network.forge;

import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.core.extension.forge.FMLHandshakeHandlerExtensions;
import io.netty.util.AttributeKey;
import net.minecraft.network.Connection;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedForgePacketContext implements PollinatedPacketContext {

    private final SimpleChannel channel;
    private final Supplier<NetworkEvent.Context> ctx;

    public PollinatedForgePacketContext(SimpleChannel channel, Supplier<NetworkEvent.Context> ctx) {
        this.channel = channel;
        this.ctx = ctx;
    }

    @Override
    public CompletableFuture<Void> enqueueWork(Runnable runnable) {
        return this.ctx.get().enqueueWork(runnable);
    }

    @Override
    public void waitFor(Future<?> future) {
        Connection connection = this.getNetworkManager();
        if (connection.getPacketListener() instanceof FMLHandshakeHandlerExtensions) {
            ((FMLHandshakeHandlerExtensions) connection.channel().attr(AttributeKey.valueOf("fml:handshake")).get()).pollen_addWait(future);
        }
    }

    @Override
    public void reply(PollinatedPacket<?> packet) {
        this.channel.reply(packet, this.ctx.get());
    }

    @Override
    public PollinatedPacketDirection getDirection() {
        NetworkDirection direction = this.ctx.get().getDirection();
        switch (direction) {
            case PLAY_TO_SERVER:
                return PollinatedPacketDirection.PLAY_SERVERBOUND;
            case PLAY_TO_CLIENT:
                return PollinatedPacketDirection.PLAY_CLIENTBOUND;
            case LOGIN_TO_SERVER:
                return PollinatedPacketDirection.LOGIN_SERVERBOUND;
            case LOGIN_TO_CLIENT:
                return PollinatedPacketDirection.LOGIN_CLIENTBOUND;
            default:
                throw new IllegalStateException("Unknown network direction: " + direction);
        }
    }

    @Override
    public Connection getNetworkManager() {
        return this.ctx.get().getNetworkManager();
    }
}
