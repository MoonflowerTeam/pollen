package gg.moonflower.pollen.api.event.events.world;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Events involving explosions.
 *
 * @author abigailfails
 * @since 1.0.0
 */
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

    @FunctionalInterface
    public interface Start {
        /**
         * Fired when an explosion is about to occur
         *
         * @param level     The level of the explosion
         * @param explosion The explosion
         * @return Whether the explosion should continue
         */
        boolean start(Level level, Explosion explosion);
    }

    @FunctionalInterface
    public interface Detonate {
        /**
         * Fired when an explosion has lists of affected entities, which can be changed.
         *
         * @param level      The level of the explosion
         * @param explosion  The explosion
         * @param entityList The list of entities affected.
         */
        void detonate(Level level, Explosion explosion, List<Entity> entityList);
    }
}
