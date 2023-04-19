package gg.moonflower.pollen.api.event.network.v1;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@FunctionalInterface
public interface ServerNetworkEvent {

    Event<ServerNetworkEvent> LOGIN = EventFactory.createLoop();

    Event<ServerNetworkEvent> DISCONNECT = EventFactory.createEventResult();

    void event(ServerGamePacketListenerImpl handler, MinecraftServer server);
}
