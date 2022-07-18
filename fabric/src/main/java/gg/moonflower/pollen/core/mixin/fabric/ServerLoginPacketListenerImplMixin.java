package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.core.extensions.fabric.ServerLoginPacketListenerImplExtension;
import gg.moonflower.pollen.core.mixin.ClientboundCustomQueryPacketAccessor;
import gg.moonflower.pollen.core.mixin.ServerboundCustomQueryPacketAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin implements ServerLoginPacketListenerImplExtension {

    @Unique
    private int queryId;
    @Unique
    private final Map<Integer, ClientboundCustomQueryPacket> trackedPackets = new Int2ObjectArrayMap<>();
    @Unique
    private final Set<Integer> delayedPackets = new HashSet<>();

    @Inject(method = "handleCustomQueryPacket", at = @At("HEAD"))
    public void handleCustomQueryPacket(ServerboundCustomQueryPacket packet, CallbackInfo ci) {
        this.queryId = ((ServerboundCustomQueryPacketAccessor) packet).getTransactionId();
    }

    @Override
    public void pollen_trackPacket(ClientboundCustomQueryPacket packet) {
        this.trackedPackets.put(((ClientboundCustomQueryPacketAccessor) packet).getTransactionId(), packet);
    }

    @Override
    public void pollen_delayPacket() {
        this.delayedPackets.add(this.queryId);
    }

    @Override
    public void pollen_flushDelayedPackets(ServerGamePacketListenerImpl listener) {
        this.delayedPackets.forEach(packetId -> {
            ClientboundCustomQueryPacket loginPacket = this.trackedPackets.remove(packetId);
            if (loginPacket == null)
                return;

            ClientboundCustomQueryPacketAccessor accessor = (ClientboundCustomQueryPacketAccessor) loginPacket;
            ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(accessor.getIdentifier(), accessor.getData());
            listener.send(packet);
        });
        this.trackedPackets.clear();
        this.delayedPackets.clear();
    }
}
