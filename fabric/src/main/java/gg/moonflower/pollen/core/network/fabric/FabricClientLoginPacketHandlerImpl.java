package gg.moonflower.pollen.core.network.fabric;

import gg.moonflower.pollen.api.config.fabric.ConfigTracker;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.network.packet.login.ServerboundAckPacket;
import gg.moonflower.pollen.core.network.PollenClientLoginPacketHandlerImpl;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FabricClientLoginPacketHandlerImpl extends PollenClientLoginPacketHandlerImpl implements FabricClientLoginPacketHandler {

    @Override
    public void handleClientboundSyncConfigDataPacket(ClientboundSyncConfigDataPacket msg, PollinatedPacketContext ctx) {
        ConfigTracker.INSTANCE.receiveSyncedConfig(msg, ctx);
        ctx.reply(new ServerboundAckPacket());
    }
}
