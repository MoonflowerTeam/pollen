package gg.moonflower.pollen.api.event.events.world;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.List;

public final class ExplosionEvents {

    public static final PollinatedEvent<Start> START = EventRegistry.create(Start.class, events -> (level, explosion) -> {
        for (Start event : events)
            if (!event.start(level, explosion))
                return false;
        return true;
    });
    public static final PollinatedEvent<Detonate> DETONATE = EventRegistry.createLoop(Detonate.class);

    private ExplosionEvents() {
    }

    /**
     * Fired when an explosion is about to occur
     *
     * @author abigailfails
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Start {

        /**
         * Called at the start of every explosion.
         *
         * @param level     The level the explosion is in
         * @param explosion The explosion itself
         * @return <code>true</code> to continue exploding, or <code>false</code> to cancel the explosion completely
         */
        boolean start(Level level, Explosion explosion);
    }

    /**
     * Fired when an explosion has lists of affected entities, which can be changed.
     *
     * @author abigailfails
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Detonate {

        /**
         * Called when an explosion has lists of affected entities, which can be changed.
         *
         * @param level      The level the explosion is in
         * @param explosion  The explosion itself
         * @param entityList The list of entities affected.
         */
        void detonate(Level level, Explosion explosion, List<Entity> entityList);
    }
}
