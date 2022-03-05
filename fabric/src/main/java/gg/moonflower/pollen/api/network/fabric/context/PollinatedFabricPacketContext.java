package gg.moonflower.pollen.api.network.fabric.context;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.api.platform.Platform;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.util.thread.BlockableEventLoop;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@ApiStatus.Internal
public abstract class PollinatedFabricPacketContext implements PollinatedPacketContext {

    private final Connection connection;
    private final ServerLoginNetworking.LoginSynchronizer synchronizer;
    private final PollinatedPacketDirection direction;

    protected PollinatedFabricPacketContext(Connection connection, ServerLoginNetworking.LoginSynchronizer synchronizer, PollinatedPacketDirection direction) {
        this.connection = connection;
        this.synchronizer = synchronizer;
        this.direction = direction;
    }

    @Override
    public CompletableFuture<Void> enqueueWork(Runnable runnable) { // Try to get the server executor if server bound, otherwise try to find any executor
        return (this.direction.isServerbound() ? Platform.getRunningServer().<BlockableEventLoop<?>>map(__ -> __).orElseGet(Platform::getGameExecutor) : Platform.getGameExecutor()).submit(runnable);
    }

    @Override
    public void waitFor(Future<?> future) {
        this.synchronizer.waitFor(future);
    }

    @Override
    public void disconnect(Component message) {
        switch (this.direction) {
            case PLAY_SERVERBOUND -> {
                this.connection.send(new ClientboundDisconnectPacket(message), future -> this.connection.disconnect(message));
                this.connection.setReadOnly();
                Platform.getRunningServer().ifPresent(server -> server.executeBlocking(this.connection::handleDisconnection));
            }
            case LOGIN_SERVERBOUND -> {
                this.connection.send(new ClientboundLoginDisconnectPacket(message), future -> this.connection.disconnect(message));
                this.connection.setReadOnly();
                Platform.getRunningServer().ifPresent(server -> server.executeBlocking(this.connection::handleDisconnection));
            }
            case PLAY_CLIENTBOUND, LOGIN_CLIENTBOUND -> this.connection.disconnect(message);
        }
    }

    @Override
    public PollinatedPacketDirection getDirection() {
        return direction;
    }

    @Override
    public Connection getNetworkManager() {
        return connection;
    }
}
