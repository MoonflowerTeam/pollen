package gg.moonflower.pollen.core.network.forge;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.sync.forge.SyncedDataManagerImpl;
import gg.moonflower.pollen.core.network.PollenClientPlayPacketHandlerImpl;
import gg.moonflower.pollen.core.network.play.PollenClientPlayPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ForgeClientPlayPacketHandlerImpl extends PollenClientPlayPacketHandlerImpl implements ForgeClientPlayPacketHandler, PollenClientPlayPacketHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void handleClientboundUpdateSyncedDataPacket(ClientboundUpdateSyncedDataPacket msg, PollinatedPacketContext ctx) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;

        ctx.enqueueWork(() -> {
            Entity entity = level.getEntity(msg.getEntityId());
            if (entity == null) {
                LOGGER.warn("Server sent synced data for unexpected entity: " + msg.getEntityId());
                return;
            }

            SyncedDataManagerImpl.readPacketData(msg.getPayload(), entity);
        });
    }
}
