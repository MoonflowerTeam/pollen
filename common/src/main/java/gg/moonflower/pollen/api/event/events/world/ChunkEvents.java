package gg.moonflower.pollen.api.event.events.world;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class ChunkEvents {

    public static final PollinatedEvent<Load> LOAD = EventRegistry.createLoop(Load.class);
    public static final PollinatedEvent<Unload> UNLOAD = EventRegistry.createLoop(Unload.class);
    private ChunkEvents() {
    }

    @FunctionalInterface
    public interface Load {
        void load(LevelAccessor level, ChunkAccess chunk);
    }

    @FunctionalInterface
    public interface Unload {
        void unload(LevelAccessor level, ChunkAccess chunk);
    }
}
