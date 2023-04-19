package gg.moonflower.pollen.api.event.level.v1;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

@FunctionalInterface
public interface ChunkTrackingEvent {

    Event<ChunkTrackingEvent> START_TRACKING = EventFactory.createLoop();

    Event<ChunkTrackingEvent> STOP_TRACKING = EventFactory.createLoop();

    void event(ServerPlayer player, ChunkPos pos);
}
