package gg.moonflower.pollen.core.network;

import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.core.network.play.ClientboundUpdateSettingsPacket;
import gg.moonflower.pollen.core.network.play.PollenServerPlayPacketHandler;
import gg.moonflower.pollen.core.network.play.ServerboundUpdateSettingsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class PollenServerPlayPacketHandlerImpl implements PollenServerPlayPacketHandler {

    @Override
    public void handleUpdateSettingsPacket(ServerboundUpdateSettingsPacket msg, PollinatedPacketContext ctx) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;

        ServerPlayer sender = ctx.getSender();
        if (sender == null)
            return;

        ctx.enqueueWork(() -> PollenMessages.PLAY.sendToTracking(sender, new ClientboundUpdateSettingsPacket(sender, msg.getEntitlement(), msg.getSettings())));
    }
}
