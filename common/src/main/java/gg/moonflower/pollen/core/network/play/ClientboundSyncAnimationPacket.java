package gg.moonflower.pollen.core.network.play;

import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.pinwheel.api.common.animation.AnimatedEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class ClientboundSyncAnimationPacket implements PollinatedPacket<PollenClientPlayPacketHandler> {

    private final int entityId;
    private final int animationId;
    private final int duration;

    public <T extends Entity & AnimatedEntity> ClientboundSyncAnimationPacket(T entity, int duration) {
        this.entityId = entity.getId();
        this.animationId = ArrayUtils.indexOf(entity.getAnimationStates(), entity.getTransitionAnimationState());
        this.duration = duration;
    }

    public ClientboundSyncAnimationPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.animationId = buf.readVarInt();
        this.duration = buf.readVarInt();
    }

    @Override
    public void writePacketData(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.animationId);
        buf.writeVarInt(this.duration);
    }

    @Override
    public void processPacket(PollenClientPlayPacketHandler handler, PollinatedPacketContext ctx) {
        handler.handleSyncAnimationPacket(this, ctx);
    }

    public int getEntityId() {
        return entityId;
    }

    public int getAnimationId() {
        return animationId;
    }

    public int getDuration() {
        return duration;
    }
}
