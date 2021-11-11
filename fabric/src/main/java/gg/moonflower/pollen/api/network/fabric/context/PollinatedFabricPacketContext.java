package gg.moonflower.pollen.api.network.fabric.context;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.api.platform.Platform;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.Connection;
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
    public CompletableFuture<Void> enqueueWork(Runnable runnable) {
        return Platform.getGameExecutor().submit(runnable);
    }

    @Override
    public void waitFor(Future<?> future) {
        this.synchronizer.waitFor(future);
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
