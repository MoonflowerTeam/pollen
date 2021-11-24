package gg.moonflower.pollen.api.event.events.world;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface ChunkEvent {

    PollinatedEvent<Load> LOAD = EventRegistry.createLoop(Load.class);
    PollinatedEvent<Unload> UNLOAD = EventRegistry.createLoop(Unload.class);

    @FunctionalInterface
    interface Load {
        void load(LevelAccessor level, ChunkAccess chunk);
    }

    @FunctionalInterface
    interface Unload {
        void unload(LevelAccessor level, ChunkAccess chunk);
    }
}
