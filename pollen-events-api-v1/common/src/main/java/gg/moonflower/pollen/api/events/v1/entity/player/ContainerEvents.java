package gg.moonflower.pollen.api.events.v1.entity.player;

import gg.moonflower.pollen.api.base.event.EventRegistry;
import gg.moonflower.pollen.api.base.event.PollinatedEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class ContainerEvents {

    public static final PollinatedEvent<Open> OPEN = EventRegistry.createLoop(Open.class);
    public static final PollinatedEvent<Close> CLOSE = EventRegistry.createLoop(Close.class);

    private ContainerEvents() {
    }

    /**
     * Fired when a player opens a container.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Open {

        /**
         * Called when the specified player opens the specified container.
         *
         * @param player    The player opening the container
         * @param container The container being opened
         */
        void open(Player player, AbstractContainerMenu container);
    }

    /**
     * Fired when a player closes a container.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Close {

        /**
         * Called when the specified player closes the specified container.
         *
         * @param player    The player closing the container
         * @param container The container being closed
         */
        void close(Player player, AbstractContainerMenu container);
    }
}
