package gg.moonflower.pollen.core.network.forge;

import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.sync.forge.SyncedDataManagerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class ClientboundUpdateSyncedDataPacket implements PollinatedPacket<ForgeClientPlayPacketHandler> {

    private Entity provider;
    private Entity player;
    private boolean sync;

    @OnlyIn(Dist.CLIENT)
    private int entityId;
    @OnlyIn(Dist.CLIENT)
    private FriendlyByteBuf payload;

    public ClientboundUpdateSyncedDataPacket(Entity provider, Entity player, boolean sync) {
        this.provider = provider;
        this.player = player;
        this.sync = sync;
    }

    public ClientboundUpdateSyncedDataPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.payload = buf;
    }

    @Override
    public void writePacketData(FriendlyByteBuf buf) {
        buf.writeVarInt(this.provider.getId());
        SyncedDataManagerImpl.writePacketData(buf, this.provider, this.player, this.sync);
    }

    @Override
    public void processPacket(ForgeClientPlayPacketHandler handler, PollinatedPacketContext ctx) {
        handler.handleClientboundUpdateSyncedDataPacket(this, ctx);
    }

    @OnlyIn(Dist.CLIENT)
    public int getEntityId() {
        return entityId;
    }

    @OnlyIn(Dist.CLIENT)
    public FriendlyByteBuf getPayload() {
        return payload;
    }
}
