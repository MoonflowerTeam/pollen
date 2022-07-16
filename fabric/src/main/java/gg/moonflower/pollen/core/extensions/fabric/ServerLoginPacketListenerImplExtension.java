package gg.moonflower.pollen.core.extensions.fabric;

import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public interface ServerLoginPacketListenerImplExtension {

    void pollen_trackPacket(ClientboundCustomQueryPacket packet);

    void pollen_delayPacket();

    void pollen_flushDelayedPackets(ServerGamePacketListenerImpl listener);
}
