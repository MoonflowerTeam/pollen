package gg.moonflower.pollen.api.event.events.entity.player;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;


public final class PlayerEvents {

    public static final PollinatedEvent<PlayerLoggedInEvent> LOGGED_IN_EVENT = EventRegistry.createLoop(PlayerLoggedInEvent.class);
    public static final PollinatedEvent<PlayerLoggedOutEvent> LOGGED_OUT_EVENT = EventRegistry.createLoop(PlayerLoggedOutEvent.class);
    public static final PollinatedEvent<PlayerAdvancementEvent> ADVANCEMENT_EVENT = EventRegistry.createLoop(PlayerAdvancementEvent.class);
    public static final PollinatedEvent<ExpPickup> EXP_PICKUP = EventRegistry.createCancellable(ExpPickup.class);
    public static final PollinatedEvent<StartSleeping> START_SLEEPING = EventRegistry.create(StartSleeping.class, events -> (player, pos) -> {
        for (StartSleeping event : events) {
            Player.BedSleepingProblem result = event.startSleeping(player, pos);
            if (result != null)
                return result;
        }
        return null;
    });
    public static final PollinatedEvent<StopSleeping> STOP_SLEEPING = EventRegistry.createLoop(StopSleeping.class);

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


    /**
     * Fired when a player picks up experience orbs.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface ExpPickup {

        /**
         * Called after the specified player collides with the specified orb, before the player is awarded the EXP.
         *
         * @param player The player that collided with the specified orb
         * @param orb    The {@link ExperienceOrb} being picked up by the player
         * @return <code>true</code> to continue the process, or <code>false</code> to cancel it
         */
        boolean expPickup(Player player, ExperienceOrb orb);
    }

    /**
     * Fired when a player starts sleeping.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface StartSleeping {

        /**
         * Called when the specified player starts sleeping at the given {@link BlockPos}.
         *
         * @param player The player falling asleep
         * @param pos    The sleeping position of the player
         * @return A {@link Player.BedSleepingProblem} result for this interaction
         */
        Player.BedSleepingProblem startSleeping(Player player, BlockPos pos);
    }

    /**
     * Fired when a player stops sleeping and wakes up.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface StopSleeping {

        /**
         * Called when the specified player wakes up.
         *
         * @param player The player waking up
         * @param wakeImmediately Whether the player is waking up immediately
         * @param updateLevel Whether the list of sleeping players is being updated
         */
        void stopSleeping(Player player, boolean wakeImmediately, boolean updateLevel);
    }
}
