package gg.moonflower.pollen.api.event.events.entity.player;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import gg.moonflower.pollen.api.util.value.MutableInt;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;


public final class PlayerEvents {

    public static final PollinatedEvent<PlayerLoggedInEvent> LOGGED_IN_EVENT = EventRegistry.createLoop(PlayerLoggedInEvent.class);
    public static final PollinatedEvent<PlayerLoggedOutEvent> LOGGED_OUT_EVENT = EventRegistry.createLoop(PlayerLoggedOutEvent.class);
    public static final PollinatedEvent<PlayerAdvancementEvent> ADVANCEMENT_EVENT = EventRegistry.createLoop(PlayerAdvancementEvent.class);
    public static final PollinatedEvent<ExpPickup> EXP_PICKUP = EventRegistry.createCancellable(ExpPickup.class);
    public static final PollinatedEvent<ExpChange> EXP_CHANGE = EventRegistry.createCancellable(ExpChange.class);
    public static final PollinatedEvent<LevelChange> LEVEL_CHANGE = EventRegistry.createCancellable(LevelChange.class);
    public static final PollinatedEvent<StartSleeping> START_SLEEPING = EventRegistry.create(StartSleeping.class, events -> (player, pos) -> {
        for (StartSleeping event : events) {
            Player.BedSleepingProblem result = event.startSleeping(player, pos);
            if (result != null)
                return result;
        }
        return null;
    });
    public static final PollinatedEvent<StopSleeping> STOP_SLEEPING = EventRegistry.createLoop(StopSleeping.class);
    public static final PollinatedEvent<Respawn> RESPAWN = EventRegistry.createLoop(Respawn.class);
    public static final PollinatedEvent<Clone> CLONE = EventRegistry.createLoop(Clone.class);
    public static final PollinatedEvent<ItemCrafted> ITEM_CRAFTED = EventRegistry.createLoop(ItemCrafted.class);
    public static final PollinatedEvent<ItemSmelted> ITEM_SMELTED = EventRegistry.createLoop(ItemSmelted.class);

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
     * Fired when a player is about to be awarded experience.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface ExpChange {

        /**
         * Called before the specified player is given experience, allowing the amount to be modified.
         *
         * @param player       The player being given experience
         * @param modifiableXp The modifiable amount of experience to give to the player
         * @return <code>true</code> to continue, or <code>false</code> to stop further processing
         */
        boolean expChange(Player player, MutableInt modifiableXp);
    }

    /**
     * Fired when a player is about to be awarded experience levels.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface LevelChange {

        /**
         * Called before the specified player is given levels, allowing for the amount to be modified.
         *
         * @param player           The player being given experience levels
         * @param modifiableLevels The modifiable amount of levels to give to the player
         * @return <code>true</code> to continue, or <code>false</code> to stop further processing
         */
        boolean levelChange(Player player, MutableInt modifiableLevels);
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
         * @return <code>null</code> to allow the player to sleep, or a {@link Player.BedSleepingProblem} if they cannot sleep
         */

        @Nullable
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

    /**
     * Fired when a player respawns.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface Respawn {

        /**
         * Called when the specified player is about to respawn.
         *
         * @param player       The player respawning
         * @param endConquered Whether the player has won the game and is leaving the end
         */
        void respawn(ServerPlayer player, boolean endConquered);
    }

    /**
     * Fired when a player is cloned.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface Clone {

        /**
         * Called when the specified player is cloned, usually due to respawning or changing dimensions.
         *
         * @param originalPlayer The original {@link ServerPlayer} being cloned
         * @param player         The cloned player
         * @param wasDeath       Whether the player died
         */
        void clone(ServerPlayer originalPlayer, ServerPlayer player, boolean wasDeath);
    }

    /**
     * Fired when a player crafts an item.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface ItemCrafted {

        /**
         * Called when the specified player takes the finished item out of the output slot.
         *
         * @param player    The player taking the item
         * @param stack     The item that has been crafteed
         * @param container The crafting table's inventory
         */
        void craft(Player player, ItemStack stack, Container container);
    }

    /**
     * Fired when a player smelts an item.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface ItemSmelted {

        /**
         * Called when the specified player takes the given item out of the furnace's output slot.
         *
         * @param player The player taking the smelted  item
         * @param stack  The smelted item
         */
        void smelt(Player player, ItemStack stack);
    }
}
