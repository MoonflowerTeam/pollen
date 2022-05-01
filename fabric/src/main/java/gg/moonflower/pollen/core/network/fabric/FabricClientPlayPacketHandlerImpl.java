package gg.moonflower.pollen.core.network.fabric;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.core.network.PollenClientPlayPacketHandlerImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FabricClientPlayPacketHandlerImpl extends PollenClientPlayPacketHandlerImpl implements FabricClientPlayPacketHandler {

    @Override
    public void handleClientboundSpawnEntityPacket(ClientboundSpawnEntityPacket msg, PollinatedPacketContext ctx) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;

        ctx.enqueueWork(() -> {
            EntityType<?> type = msg.getType();
            if (type == null)
                throw new IllegalStateException(String.format("Could not spawn entity (id %d) with unknown type at (%f, %f, %f)", msg.getEntityId(), msg.getX(), msg.getY(), msg.getZ()));

            Entity e = type.create(level);
            if (e == null)
                return;

            e.setPacketCoordinates(msg.getX(), msg.getY(), msg.getZ());
            e.absMoveTo(msg.getX(), msg.getY(), msg.getZ(), msg.getRotationY(), msg.getRotationX());
            e.setYHeadRot(msg.getHeadRotationY());
            e.setYBodyRot(msg.getHeadRotationY());

            e.setId(msg.getEntityId());
            e.setUUID(msg.getUuid());
            level.putNonPlayerEntity(msg.getEntityId(), e);
            e.lerpMotion(msg.getMotionX(), msg.getMotionY(), msg.getMotionZ());
        });
    }
}
