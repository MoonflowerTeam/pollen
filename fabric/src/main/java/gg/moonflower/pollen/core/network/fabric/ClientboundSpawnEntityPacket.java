package gg.moonflower.pollen.core.network.fabric;

import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class ClientboundSpawnEntityPacket implements PollinatedPacket<FabricClientPlayPacketHandler> {

    private final int typeId;
    private final int entityId;
    private final UUID uuid;
    private final double x;
    private final double y;
    private final double z;
    private final byte xRot;
    private final byte yRot;
    private final byte yHeadRot;
    private final int velX;
    private final int velY;
    private final int velZ;

    public ClientboundSpawnEntityPacket(Entity entity) {
        this.typeId = Registry.ENTITY_TYPE.getId(entity.getType());
        this.entityId = entity.getId();
        this.uuid = entity.getUUID();
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.xRot = (byte) Mth.floor(entity.xRot * 256.0F / 360.0F);
        this.yRot = (byte) Mth.floor(entity.yRot * 256.0F / 360.0F);
        this.yHeadRot = (byte) (entity.getYHeadRot() * 256.0F / 360.0F);
        Vec3 vec3d = entity.getDeltaMovement();
        double d1 = Mth.clamp(vec3d.x, -3.9D, 3.9D);
        double d2 = Mth.clamp(vec3d.y, -3.9D, 3.9D);
        double d3 = Mth.clamp(vec3d.z, -3.9D, 3.9D);
        this.velX = (int) (d1 * 8000.0D);
        this.velY = (int) (d2 * 8000.0D);
        this.velZ = (int) (d3 * 8000.0D);
    }

    public ClientboundSpawnEntityPacket(FriendlyByteBuf buf) {
        this.typeId = buf.readVarInt();
        this.entityId = buf.readInt();
        this.uuid = buf.readUUID();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.xRot = buf.readByte();
        this.yRot = buf.readByte();
        this.yHeadRot = buf.readByte();
        this.velX = buf.readShort();
        this.velY = buf.readShort();
        this.velZ = buf.readShort();
    }

    @Override
    public void writePacketData(FriendlyByteBuf buf) {
        buf.writeVarInt(this.typeId);
        buf.writeInt(this.entityId);
        buf.writeUUID(this.uuid);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.xRot);
        buf.writeByte(this.yRot);
        buf.writeByte(this.yHeadRot);
        buf.writeShort(this.velX);
        buf.writeShort(this.velY);
        buf.writeShort(this.velZ);
    }

    @Override
    public void processPacket(FabricClientPlayPacketHandler handler, PollinatedPacketContext ctx) {
        handler.handleClientboundSpawnEntityPacket(this, ctx);
    }

    public EntityType<?> getType() {
        return Registry.ENTITY_TYPE.byId(this.typeId);
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getRotationX() {
        return (this.xRot * 360) / 256.0F;
    }

    public float getRotationY() {
        return (this.yRot * 360) / 256.0F;
    }

    public float getHeadRotationY() {
        return (this.yHeadRot * 360) / 256.0F;
    }

    public double getMotionX() {
        return this.velX / 8000.0;
    }

    public double getMotionY() {
        return this.velY / 8000.0;
    }

    public double getMotionZ() {
        return this.velZ / 8000.0;
    }
}
