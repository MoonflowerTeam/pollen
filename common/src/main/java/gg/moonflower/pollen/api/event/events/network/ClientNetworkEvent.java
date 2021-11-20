package gg.moonflower.pollen.api.event.events.network;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import org.jetbrains.annotations.Nullable;

/**
 * Events fired for client connection status.
 */
public class ClientNetworkEvent extends PollinatedEvent {

    private final MultiPlayerGameMode controller;
    private final LocalPlayer player;
    private final Connection connection;

    private ClientNetworkEvent(MultiPlayerGameMode controller, LocalPlayer player, Connection connection) {
        this.controller = controller;
        this.player = player;
        this.connection = connection;
    }

    /**
     * @return the player controller
     */
    @Nullable
    public MultiPlayerGameMode getController() {
        return controller;
    }

    /**
     * @return the local player instance
     */
    @Nullable
    public LocalPlayer getPlayer() {
        return player;
    }

    /**
     * @return the network connection to the server
     */
    @Nullable
    public Connection getConnection() {
        return connection;
    }

    /**
     * Called each time the client creates a play connection. Eg joins a server or world
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class LoggedIn extends ClientNetworkEvent {
        public LoggedIn(MultiPlayerGameMode controller, LocalPlayer player, Connection connection) {
            super(controller, player, connection);
        }
    }

    /**
     * Called each time the client closes a play connection. Eg exits the game
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class LoggedOut extends ClientNetworkEvent {
        public LoggedOut(MultiPlayerGameMode controller, LocalPlayer player, Connection connection) {
            super(controller, player, connection);
        }
    }

    /**
     * Called each time the client player is cloned.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Respawn extends ClientNetworkEvent {

        private final LocalPlayer oldPlayer;

        public Respawn(MultiPlayerGameMode controller, LocalPlayer oldPlayer, LocalPlayer player, Connection connection) {
            super(controller, player, connection);
            this.oldPlayer = oldPlayer;
        }

        /**
         * @return The old player entity instance
         */
        public LocalPlayer getOldPlayer() {
            return oldPlayer;
        }
    }
}
