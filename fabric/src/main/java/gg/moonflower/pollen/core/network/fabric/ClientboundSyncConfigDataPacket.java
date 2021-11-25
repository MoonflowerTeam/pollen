package gg.moonflower.pollen.core.network.fabric;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.network.packet.login.SimplePollinatedLoginPacket;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class ClientboundSyncConfigDataPacket extends SimplePollinatedLoginPacket<FabricClientLoginPacketHandler> {

    private final String fileName;
    private final byte[] fileData;

    public ClientboundSyncConfigDataPacket(String fileName, byte[] fileData) {
        this.fileName = fileName;
        this.fileData = fileData;
    }

    public ClientboundSyncConfigDataPacket(FriendlyByteBuf buf) {
        this.fileName = buf.readUtf();
        this.fileData = buf.readByteArray();
    }

    @Override
    public void writePacketData(FriendlyByteBuf buf) {
        buf.writeUtf(this.fileName);
        buf.writeByteArray(this.fileData);
    }

    @Override
    public void processPacket(FabricClientLoginPacketHandler handler, PollinatedPacketContext ctx) {
        handler.handleClientboundSyncConfigDataPacket(this, ctx);
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }
}
