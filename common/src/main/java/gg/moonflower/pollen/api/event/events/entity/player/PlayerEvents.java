package gg.moonflower.pollen.api.event.events.entity.player;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public final class PlayerEvents {

    public static final PollinatedEvent<PlayerLoggedInEvent> LOGGED_IN_EVENT = EventRegistry.createLoop(PlayerLoggedInEvent.class);
    public static final PollinatedEvent<PlayerLoggedOutEvent> LOGGED_OUT_EVENT = EventRegistry.createLoop(PlayerLoggedOutEvent.class);
    public static final PollinatedEvent<PlayerAdvancementEvent> ADVANCEMENT_EVENT = EventRegistry.createLoop(PlayerAdvancementEvent.class);

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

    /**
     * Fired when a player is awarded an advancement.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface PlayerAdvancementEvent {

        /**
         * Called when the specified player is awarded the given advancement.
         *
         * @param player      The player being awarded the advancement
         * @param advancement The advancement being awarded
         */
        void playerAdvancement(Player player, Advancement advancement);
    }
}
