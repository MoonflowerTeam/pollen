package gg.moonflower.pollen.api.event.events.network;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;

public interface ClientNetworkEvent {

    PollinatedEvent<Login> LOGIN = EventRegistry.createLoop(Login.class);
    PollinatedEvent<Logout> LOGOUT = EventRegistry.createLoop(Logout.class);
    PollinatedEvent<Respawn> RESPAWN = EventRegistry.createLoop(Respawn.class);

    @FunctionalInterface
    interface Login {
        void login(MultiPlayerGameMode controller, LocalPlayer player, Connection connection);
    }

    @FunctionalInterface
    interface Logout {
        void logout(MultiPlayerGameMode controller, LocalPlayer player, Connection connection);
    }

    @FunctionalInterface
    interface Respawn {
        void respawn(MultiPlayerGameMode controller, LocalPlayer oldPlayer, LocalPlayer player, Connection connection);
    }

}