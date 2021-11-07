package gg.moonflower.pollen.pinwheel.core.common.network;

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
public class ClientboundSyncAnimationMessage
{
    private final int entityId;
    private final int animationId;

    public <T extends Entity & AnimatedEntity> ClientboundSyncAnimationMessage(T entity)
    {
        this.entityId = entity.getId();
        this.animationId = ArrayUtils.indexOf(entity.getAnimationStates(), entity.getAnimationState());
    }

    public ClientboundSyncAnimationMessage(FriendlyByteBuf buf)
    {
        this.entityId = buf.readVarInt();
        this.animationId = buf.readVarInt();
    }

    public void write(FriendlyByteBuf buf)
    {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.animationId);
    }

    @Environment(EnvType.CLIENT)
    public int getEntityId()
    {
        return entityId;
    }

    @Environment(EnvType.CLIENT)
    public int getAnimationId()
    {
        return animationId;
    }
}
