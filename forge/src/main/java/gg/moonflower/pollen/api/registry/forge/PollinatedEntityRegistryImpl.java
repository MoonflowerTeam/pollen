package gg.moonflower.pollen.api.registry.forge;

import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollinatedEntityRegistryImpl {

    public static Packet<?> createSpawnEntityPacket(Entity entity) {
        return NetworkHooks.getEntitySpawningPacket(entity);
    }
}
