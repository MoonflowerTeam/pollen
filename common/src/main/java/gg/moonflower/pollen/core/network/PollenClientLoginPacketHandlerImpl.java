package gg.moonflower.pollen.core.network;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.network.packet.login.ServerboundAckPacket;
import gg.moonflower.pollen.api.sync.SyncedDataManager;
import gg.moonflower.pollen.core.network.login.ClientboundSyncPlayerDataKeysPacket;
import gg.moonflower.pollen.core.network.login.PollenClientLoginPacketHandler;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class PollenClientLoginPacketHandlerImpl implements PollenClientLoginPacketHandler {

    @Override
    public void handleSyncPlayerDataKeysPacket(ClientboundSyncPlayerDataKeysPacket pkt, PollinatedPacketContext ctx) {
        SyncedDataManager.syncKeys(pkt);
        ctx.reply(new ServerboundAckPacket());
    }
}
