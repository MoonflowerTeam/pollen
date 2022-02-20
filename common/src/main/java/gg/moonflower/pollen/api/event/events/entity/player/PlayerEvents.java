package gg.moonflower.pollen.api.event.events.entity.player;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerEvents {

    public static final PollinatedEvent<PlayerLoggedInEvent> LOGGED_IN_EVENT = EventRegistry.createLoop(PlayerLoggedInEvent.class);
    public static final PollinatedEvent<PlayerLoggedOutEvent> LOGGED_OUT_EVENT = EventRegistry.createLoop(PlayerLoggedOutEvent.class);

    private PlayerEvents() {
    }

    /**
     * Fired for each player that logs in on the server side.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface PlayerLoggedInEvent {

        /**
         * Called when the specified player logs in.
         *
         * @param player The player logging in
         */
        void playerLoggedIn(ServerPlayer player);
    }

    /**
     * Fired for each player that logs out on the server side.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface PlayerLoggedOutEvent {

        /**
         * Called when the specified player logs out.
         *
         * @param player The player logging out
         */
        void playerLoggedOut(ServerPlayer player);
    }
}
