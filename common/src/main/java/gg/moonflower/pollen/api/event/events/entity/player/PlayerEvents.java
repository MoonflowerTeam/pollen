package gg.moonflower.pollen.api.event.events.entity.player;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.event.events.world.ExplosionEvents;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public final class PlayerEvents {

    public static final PollinatedEvent<PlayerLoggedInEvent> LOGGED_IN_EVENT = EventRegistry.createLoop(PlayerLoggedInEvent.class);
    public static final PollinatedEvent<PlayerLoggedOutEvent> LOGGED_OUT_EVENT = EventRegistry.createLoop(PlayerLoggedOutEvent.class);
    public static final PollinatedEvent<PlayerAdvancementEvent> ADVANCEMENT_EVENT = EventRegistry.createLoop(PlayerAdvancementEvent.class);
    public static final PollinatedEvent<ExpPickup> EXP_PICKUP = EventRegistry.create(ExpPickup.class, events -> (player, orb) -> {
        for (ExpPickup event : events)
            if (!event.expPickup(player, orb))
                return false;
        return true;
    });


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
     * Fired when a player is given experience points.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface ExpChange {

        /**
         * Called when a player is about to gain experience.
         *
         * @param player  The player gaining experience
         * @param context The context to fetch and set amounts <i>(see below)</i>
         * @return <code>true</code> to continue the process, or <code>false</code> to cancel it
         */
        boolean expChange(Player player, Context context);

        interface Context {

            /**
             * @return The amount of experience being added
             */
            int getAmount();

            /**
             * Sets the amount of experience being gained.
             *
             * @param amount The new amount of EXP to set
             */
            void setAmount(int amount);
        }
    }

    @FunctionalInterface
    public interface ExpLevelChange {

        boolean expLevelChange(Player player, int levels, Context context);

        interface Context {

            /**
             * Sets a new amount of experience levels.
             *
             * @param levels The new EXP level to set
             */
            void setLevels(int levels);
        }
    }


}
