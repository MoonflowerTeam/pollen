package gg.moonflower.pollen.core.extensions.fabric;

import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public interface ServerLoginPacketListenerImplExtension {

    void pollen$trackPacket(ClientboundCustomQueryPacket packet);

    void pollen$delayPacket();

    void pollen$flushDelayedPackets(ServerGamePacketListenerImpl listener);
}
