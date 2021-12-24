package gg.moonflower.pollen.api.event.events.entity.player.server;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;

/**
 * Events for tracking players.
 */
public final class ServerPlayerTrackingEvents {

    public static final PollinatedEvent<StartTrackingChunk> START_TRACKING_CHUNK = EventRegistry.createLoop(StartTrackingChunk.class);
    public static final PollinatedEvent<StopTrackingChunk> STOP_TRACKING_CHUNK = EventRegistry.createLoop(StopTrackingChunk.class);
    public static final PollinatedEvent<StartTrackingEntity> START_TRACKING_ENTITY = EventRegistry.createLoop(StartTrackingEntity.class);
    public static final PollinatedEvent<StopTrackingEntity> STOP_TRACKING_ENTITY = EventRegistry.createLoop(StopTrackingEntity.class);

    private ServerPlayerTrackingEvents() {
    }

    /**
     * Called each time a player starts tracking a chunk.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface StartTrackingChunk {
        void startTracking(Player player, ChunkPos chunkPos);
    }

    /**
     * Called each time a player stops tracking a chunk.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface StopTrackingChunk {
        void stopTracking(Player player, ChunkPos chunkPos);
    }

    /**
     * Called each time a player starts tracking an entity.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface StartTrackingEntity {
        void startTracking(Player player, Entity entity);
    }

    /**
     * Called each time a player stops tracking an entity.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface StopTrackingEntity {
        void stopTracking(Player player, Entity entity);
    }
}
