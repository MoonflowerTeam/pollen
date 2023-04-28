package gg.moonflower.pollen.api.event.level.v1;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;

@FunctionalInterface
public interface ServerChunkLoadingEvent {

    Event<ServerChunkLoadingEvent> LOAD_CHUNK = EventFactory.createLoop();

    Event<ServerChunkLoadingEvent> UNLOAD_CHUNK = EventFactory.createLoop();

    void event(LevelAccessor level, ChunkAccess chunk);
}
