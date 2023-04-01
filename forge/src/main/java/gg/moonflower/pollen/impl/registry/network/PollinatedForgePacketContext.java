package gg.moonflower.pollen.impl.registry.network;

import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.api.platform.v1.Platform;
import gg.moonflower.pollen.core.extension.forge.FMLHandshakeHandlerExtensions;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
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
        if (connection.getPacketListener() instanceof FMLHandshakeHandlerExtensions ext) {
            ext.pollen$addWait(future);
        }
    }

    @Override
    public void disconnect(Component message) {
        Connection connection = this.getNetworkManager();
        switch (this.getDirection()) {
            case PLAY_SERVERBOUND -> {
                connection.send(new ClientboundDisconnectPacket(message), PacketSendListener.thenRun(() -> connection.disconnect(message)));
                connection.setReadOnly();
                Platform.getRunningServer().ifPresent(server -> server.executeBlocking(connection::handleDisconnection));
            }
            case LOGIN_SERVERBOUND -> {
                connection.send(new ClientboundLoginDisconnectPacket(message), PacketSendListener.thenRun(() -> connection.disconnect(message)));
                connection.setReadOnly();
                Platform.getRunningServer().ifPresent(server -> server.executeBlocking(connection::handleDisconnection));
            }
            case PLAY_CLIENTBOUND, LOGIN_CLIENTBOUND -> connection.disconnect(message);
        }
    }

    @Override
    public void reply(PollinatedPacket<?> packet) {
        this.channel.reply(packet, this.ctx.get());
    }

    @Override
    public PollinatedPacketDirection getDirection() {
        return switch (this.ctx.get().getDirection()) {
            case PLAY_TO_SERVER -> PollinatedPacketDirection.PLAY_SERVERBOUND;
            case PLAY_TO_CLIENT -> PollinatedPacketDirection.PLAY_CLIENTBOUND;
            case LOGIN_TO_SERVER -> PollinatedPacketDirection.LOGIN_SERVERBOUND;
            case LOGIN_TO_CLIENT -> PollinatedPacketDirection.LOGIN_CLIENTBOUND;
        };
    }

    @Override
    public Connection getNetworkManager() {
        return this.ctx.get().getNetworkManager();
    }
}
