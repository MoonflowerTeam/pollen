package gg.moonflower.pollen.impl.network.context.fabric;

import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacketDirection;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.Connection;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.Internal
public class PollinatedFabricLoginPacketContext extends PollinatedFabricPacketContext {

    private final Consumer<PollinatedPacket<?>> response;
    private boolean replied;

    public PollinatedFabricLoginPacketContext(Consumer<PollinatedPacket<?>> response, Connection connection, ServerLoginNetworking.LoginSynchronizer synchronizer, PollinatedPacketDirection direction) {
        super(connection, synchronizer, direction);
        this.response = response;
    }

    @Override
    public void reply(PollinatedPacket<?> packet) {
        if (this.replied) {
            throw new IllegalStateException("Cannot reply to login messages twice!");
        }
        this.replied = true;
        this.response.accept(packet);
    }
}
