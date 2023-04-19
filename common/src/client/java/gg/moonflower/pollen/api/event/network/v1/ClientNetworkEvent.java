package gg.moonflower.pollen.api.event.network.v1;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;

@FunctionalInterface
public interface ClientNetworkEvent {

    Event<ClientNetworkEvent> LOGIN = EventFactory.createLoop();

    Event<ClientNetworkEvent> DISCONNECT = EventFactory.createEventResult();

    void event(MultiPlayerGameMode controller, LocalPlayer player, Connection connection);
}
