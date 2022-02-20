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

    /**
     * Fired each time a chunk is loaded into the level.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Load {

        /**
         * Called when the specified chunk has just loaded.
         *
         * @param level The level the chunk is in
         * @param chunk The chunk loaded
         */
        void load(LevelAccessor level, ChunkAccess chunk);
    }

    /**
     * Fired each time a chunk is unloaded from the level.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Unload {

        /**
         * Called when the specified chunk has just unloaded.
         *
         * @param level The level the chunk is in
         * @param chunk The chunk unloaded
         */
        void unload(LevelAccessor level, ChunkAccess chunk);
    }
}
