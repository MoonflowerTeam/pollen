package gg.moonflower.pollen.core.network.play;

import gg.moonflower.pollen.api.animation.v1.entity.AnimatedEntity;
import gg.moonflower.pollen.api.animation.v1.controller.SerializableAnimationController;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacketContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class PollenClientMessageHandlerImpl implements PollenClientMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollenClientMessageHandler.class);

    @Override
    public void handleSetAnimation(ClientboundSetAnimationPacket packet, PollinatedPacketContext ctx) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        if (!(level.getEntity(packet.entityId()) instanceof AnimatedEntity animatedEntity)) {
            LOGGER.warn("Failed to find animated entity with id: {}", packet.entityId());
            return;
        }

        if (!(animatedEntity.getAnimationController() instanceof SerializableAnimationController controller)) {
            LOGGER.warn("Entity with id {} does not have serializable animations", packet.entityId());
            return;
        }

        ctx.enqueueWork(() -> controller.readFromNetwork(packet.data()));
    }
}
