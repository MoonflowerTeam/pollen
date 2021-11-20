package gg.moonflower.pollen.core.network.forge;

import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.sync.forge.SyncedDataManagerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class ClientboundUpdateSyncedDataPacket implements PollinatedPacket<ForgeClientPlayPacketHandler> {

    private ServerPlayer provider;
    private ServerPlayer player;
    private boolean sync;

    @OnlyIn(Dist.CLIENT)
    private int playerId;
    @OnlyIn(Dist.CLIENT)
    private FriendlyByteBuf payload;

    public ClientboundUpdateSyncedDataPacket(ServerPlayer provider, ServerPlayer player, boolean sync) {
        this.provider = provider;
        this.player = player;
        this.sync = sync;
    }

    public ClientboundUpdateSyncedDataPacket(FriendlyByteBuf buf) {
        this.playerId = buf.readVarInt();
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
    public int getPlayerId() {
        return playerId;
    }

    @OnlyIn(Dist.CLIENT)
    public FriendlyByteBuf getPayload() {
        return payload;
    }
}
