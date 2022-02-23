package gg.moonflower.pollen.core.network.play;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class ClientboundUpdateSettingsPacket implements PollinatedPacket<PollenClientPlayPacketHandler> {

    private final UUID player;
    private final String entitlement;
    private final JsonObject settings;

    public ClientboundUpdateSettingsPacket(Player player, String entitlement, JsonObject settings) {
        this.player = player.getUUID();
        this.entitlement = entitlement;
        this.settings = settings;
    }

    public ClientboundUpdateSettingsPacket(FriendlyByteBuf buf) throws IOException {
        this.player = buf.readUUID();
        this.entitlement = buf.readUtf();
        try {
            this.settings = new JsonParser().parse(buf.readUtf()).getAsJsonObject();
        } catch (JsonParseException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writePacketData(FriendlyByteBuf buf) throws IOException {
        buf.writeUUID(this.player);
        buf.writeUtf(this.entitlement);
        buf.writeUtf(this.settings.toString());
    }

    @Override
    public void processPacket(PollenClientPlayPacketHandler handler, PollinatedPacketContext ctx) {
        handler.handleUpdateSettingsPacket(this, ctx);
    }

    public UUID getPlayer() {
        return player;
    }

    public String getEntitlement() {
        return entitlement;
    }

    public JsonObject getSettings() {
        return settings;
    }
}
