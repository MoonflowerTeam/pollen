package gg.moonflower.pollen.api.event.events.network;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;

public final class ClientNetworkEvents {

    public static final PollinatedEvent<Login> LOGIN = EventRegistry.createLoop(Login.class);
    public static final PollinatedEvent<Logout> LOGOUT = EventRegistry.createLoop(Logout.class);
    public static final PollinatedEvent<Respawn> RESPAWN = EventRegistry.createLoop(Respawn.class);

    private ClientNetworkEvents() {}

    @FunctionalInterface
    public interface Login {
        void login(MultiPlayerGameMode controller, LocalPlayer player, Connection connection);
    }

    @FunctionalInterface
    public interface Logout {
        void logout(MultiPlayerGameMode controller, LocalPlayer player, Connection connection);
    }

    @FunctionalInterface
    public interface Respawn {
        void respawn(MultiPlayerGameMode controller, LocalPlayer oldPlayer, LocalPlayer player, Connection connection);
    }

}