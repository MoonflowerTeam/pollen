package gg.moonflower.pollen.core.network.login;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.network.packet.login.SimplePollinatedLoginPacket;
import gg.moonflower.pollen.api.sync.SyncedDataManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class ClientboundSyncPlayerDataKeysPacket extends SimplePollinatedLoginPacket<PollenClientLoginPacketHandler> {

    private final Map<ResourceLocation, Integer> mappings;

    public ClientboundSyncPlayerDataKeysPacket() {
        this.mappings = SyncedDataManager.getIds().collect(Collectors.toMap(id -> SyncedDataManager.byId(id).getKey(), id -> id));
    }

    public ClientboundSyncPlayerDataKeysPacket(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        this.mappings = new HashMap<>(size);
        for (int i = 0; i < size; i++)
            this.mappings.put(buf.readResourceLocation(), buf.readVarInt());
    }

    @Override
    public void writePacketData(FriendlyByteBuf buf) {
        buf.writeVarInt(this.mappings.size());
        for (Map.Entry<ResourceLocation, Integer> entry : this.mappings.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeVarInt(entry.getValue());
        }
    }

    @Override
    public void processPacket(PollenClientLoginPacketHandler handler, PollinatedPacketContext ctx) {
        handler.handleSyncPlayerDataKeysPacket(this, ctx);
    }

    public Map<ResourceLocation, Integer> getMappings() {
        return mappings;
    }
}
