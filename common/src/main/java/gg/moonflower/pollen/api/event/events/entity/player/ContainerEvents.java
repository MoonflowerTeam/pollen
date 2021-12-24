package gg.moonflower.pollen.api.event.events.entity.player;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * @author Jackson
 * @since 1.0.0
 */
public final class ContainerEvents {

    public static final PollinatedEvent<Open> OPEN = EventRegistry.createLoop(Open.class);
    public static final PollinatedEvent<Close> CLOSE = EventRegistry.createLoop(Close.class);

    private ContainerEvents() {
    }

    @FunctionalInterface
    public interface Open {
        /**
         * Fired when a player opens a container.
         *
         * @param player    The player opening the container.
         * @param container The container being opened.
         */
        void open(Player player, AbstractContainerMenu container);
    }

    @FunctionalInterface
    public interface Close {
        /**
         * Fired when a player closes a container.
         *
         * @param player    The player closing the container.
         * @param container The container being closed.
         */
        void close(Player player, AbstractContainerMenu container);
    }
}
