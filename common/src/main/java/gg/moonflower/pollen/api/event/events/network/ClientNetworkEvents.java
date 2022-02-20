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

    private ClientNetworkEvents() {
    }

    /**
     * Fired each time the local player logs in on the client side.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Login {

        /**
         * Called when the specified player joins.
         *
         * @param controller The controller for the player
         * @param player     The player logging in
         * @param connection The connection to the server
         */
        void login(MultiPlayerGameMode controller, LocalPlayer player, Connection connection);
    }

    /**
     * Fired each time the local player logs out on the client side.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Logout {

        /**
         * Called when the specified player leaves.
         *
         * @param controller The controller for the player
         * @param player     The player logging out
         * @param connection The connection to the server
         */
        void logout(MultiPlayerGameMode controller, LocalPlayer player, Connection connection);
    }

    /**
     * Fired each time the local player respawns in on the client side.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Respawn {

        /**
         * Called when the specified player is cloned because of respawning.
         *
         * @param controller The controller for the player
         * @param oldPlayer  The player from before the respawn
         * @param player     The new player created for post-respawn
         * @param connection The connection to the server
         */
        void respawn(MultiPlayerGameMode controller, LocalPlayer oldPlayer, LocalPlayer player, Connection connection);
    }
}