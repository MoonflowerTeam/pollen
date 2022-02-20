package gg.moonflower.pollen.api.event.events.entity.player.server;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;

public final class ServerPlayerTrackingEvents {

    public static final PollinatedEvent<StartTrackingChunk> START_TRACKING_CHUNK = EventRegistry.createLoop(StartTrackingChunk.class);
    public static final PollinatedEvent<StopTrackingChunk> STOP_TRACKING_CHUNK = EventRegistry.createLoop(StopTrackingChunk.class);
    public static final PollinatedEvent<StartTrackingEntity> START_TRACKING_ENTITY = EventRegistry.createLoop(StartTrackingEntity.class);
    public static final PollinatedEvent<StopTrackingEntity> STOP_TRACKING_ENTITY = EventRegistry.createLoop(StopTrackingEntity.class);

    private ServerPlayerTrackingEvents() {
    }

    /**
     * Fired each time a player starts tracking a chunk.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface StartTrackingChunk {

        /**
         * Called when the chunk at the specified pos is about to be tracked.
         *
         * @param player   The player to start sending updates to
         * @param chunkPos The position of the chunk to start tracking
         */
        void startTracking(Player player, ChunkPos chunkPos);
    }

    /**
     * Fired each time a player stops tracking a chunk.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface StopTrackingChunk {

        /**
         * Called when the chunk at the specified pos is no longer tracked.
         *
         * @param player   The player to stop sending updates to
         * @param chunkPos The position of the chunk to stop tracking
         */
        void stopTracking(Player player, ChunkPos chunkPos);
    }

    /**
     * Fired each time a player starts tracking an entity.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface StartTrackingEntity {

        /**
         * Called when the specified entity is about to be tracked.
         *
         * @param player The player to start sending updates to
         * @param entity The entity to start tracking
         */
        void startTracking(Player player, Entity entity);
    }

    /**
     * Fired each time a player stops tracking an entity.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface StopTrackingEntity {

        /**
         * Called when the specified entity is no longer tracked.
         *
         * @param player The player to stop sending updates to
         * @param entity The entity to stop tracking
         */
        void stopTracking(Player player, Entity entity);
    }
}
