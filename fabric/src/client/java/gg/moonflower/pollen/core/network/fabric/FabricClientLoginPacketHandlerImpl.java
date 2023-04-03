package gg.moonflower.pollen.core.network.fabric;

import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.network.v1.packet.login.ServerboundAckPacket;
import gg.moonflower.pollen.impl.config.fabric.ConfigTracker;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FabricClientLoginPacketHandlerImpl implements FabricClientLoginPacketHandler {

    @Override
    public void handleClientboundSyncConfigDataPacket(ClientboundSyncConfigDataPacket msg, PollinatedPacketContext ctx) {
        ConfigTracker.INSTANCE.receiveSyncedConfig(msg, ctx);
        ctx.reply(new ServerboundAckPacket());
    }
}
