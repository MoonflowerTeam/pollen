package gg.moonflower.pollen.api.event.level.v1;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

@FunctionalInterface
public interface ServerChunkLoadingEvent {

    Event<ServerChunkLoadingEvent> LOAD_CHUNK = EventFactory.createLoop();

    Event<ServerChunkLoadingEvent> UNLOAD_CHUNK = EventFactory.createLoop();

    void event(ServerLevel level, LevelChunk chunk);
}
