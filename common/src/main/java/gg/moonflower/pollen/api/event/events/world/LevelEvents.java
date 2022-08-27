package gg.moonflower.pollen.api.event.events.world;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.level.LevelAccessor;

public final class LevelEvents {

    public static final PollinatedEvent<Load> LOAD = EventRegistry.createLoop(Load.class);
    public static final PollinatedEvent<Unload> UNLOAD = EventRegistry.createLoop(Unload.class);

    private LevelEvents() {
    }

    /**
     * Fired when a level is loaded.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface Load {

        /**
         * Called when the specified level is being loaded.
         *
         * @param level The level being loaded
         */
        void load(LevelAccessor level);
    }

    /**
     * Fired when a level is unloaded.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface Unload {

        /**
         * Called when the specified level is being unloaded.
         *
         * @param level The level being unloaded
         */
        void unload(LevelAccessor level);
    }
}
