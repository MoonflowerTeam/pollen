package gg.moonflower.pollen.api.network.fabric.context;

import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import net.minecraft.network.Connection;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.Internal
public class PollinatedFabricPlayPacketContext extends PollinatedFabricPacketContext {

    private final Consumer<PollinatedPacket<?>> packetReplyer;

    public PollinatedFabricPlayPacketContext(Connection connection, Consumer<PollinatedPacket<?>> packetReplyer, PollinatedPacketDirection direction) {
        super(connection, future -> {
        }, direction);
        this.packetReplyer = packetReplyer;
    }

    @Override
    public void reply(PollinatedPacket<?> packet) {
        this.packetReplyer.accept(packet);
    }
}
