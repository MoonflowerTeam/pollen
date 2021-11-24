package gg.moonflower.pollen.api.event.events.entity.player.server;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;

public interface ServerPlayerTrackingEvent {

    PollinatedEvent<StartTrackingChunk> START_TRACKING_CHUNK = EventRegistry.createLoop(StartTrackingChunk.class);
    PollinatedEvent<StopTrackingChunk> STOP_TRACKING_CHUNK = EventRegistry.createLoop(StopTrackingChunk.class);

    @FunctionalInterface
    interface StartTrackingChunk {
        void track(Player player, ChunkPos chunkPos);
    }

    @FunctionalInterface
    interface StopTrackingChunk {
        void track(Player player, ChunkPos chunkPos);
    }
}
