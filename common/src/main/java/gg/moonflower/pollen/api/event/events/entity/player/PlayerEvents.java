package gg.moonflower.pollen.api.event.events.entity.player;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public final class PlayerEvents {

    public static final PollinatedEvent<PlayerLoggedInEvent> LOGGED_IN_EVENT = EventRegistry.createLoop(PlayerLoggedInEvent.class);
    public static final PollinatedEvent<PlayerLoggedOutEvent> LOGGED_OUT_EVENT = EventRegistry.createLoop(PlayerLoggedOutEvent.class);

    private PlayerEvents() {
    }

    /**
     * Called for each player that logs in on the server side.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public interface PlayerLoggedInEvent {
        void playerLoggedIn(ServerPlayer player);
    }

    /**
     * Called for each player that logs out on the server side.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public interface PlayerLoggedOutEvent {
        void playerLoggedOut(ServerPlayer player);
    }
}
