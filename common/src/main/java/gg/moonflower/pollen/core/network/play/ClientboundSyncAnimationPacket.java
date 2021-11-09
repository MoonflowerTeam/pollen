package gg.moonflower.pollen.core.network.play;

import gg.moonflower.pollen.api.network.message.PollinatedPacket;
import gg.moonflower.pollen.api.network.message.PollinatedPacketContext;
import gg.moonflower.pollen.pinwheel.api.common.animation.AnimatedEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

    public <T extends Entity & AnimatedEntity> ClientboundSyncAnimationPacket(T entity) {
        this.entityId = entity.getId();
        this.animationId = ArrayUtils.indexOf(entity.getAnimationStates(), entity.getAnimationState());
    }

    public ClientboundSyncAnimationPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.animationId = buf.readVarInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.animationId);
    }

    @Override
    public void writePacketData(FriendlyByteBuf buf) {

    }

    @Override
    public void processPacket(PollenClientPlayPacketHandler handler, PollinatedPacketContext ctx) {

    }

    @Environment(EnvType.CLIENT)
    public int getEntityId() {
        return entityId;
    }

    @Environment(EnvType.CLIENT)
    public int getAnimationId() {
        return animationId;
    }
}
