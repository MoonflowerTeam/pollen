package gg.moonflower.pollen.api.registry.fabric;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.core.network.fabric.ClientboundSpawnEntityPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollinatedEntityRegistryImpl {

    public static Packet<?> createSpawnEntityPacket(Entity entity) {
        return PollenMessages.PLAY.toVanillaPacket(new ClientboundSpawnEntityPacket(entity), PollinatedPacketDirection.PLAY_CLIENTBOUND);
    }
}
