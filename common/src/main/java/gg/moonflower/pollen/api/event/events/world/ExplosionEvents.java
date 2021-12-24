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
            if (event.start(level, explosion))
                return true;
        return false;
    });
    public static final PollinatedEvent<Detonate> DETONATE = EventRegistry.createLoop(Detonate.class);

    private ExplosionEvents() {
    }

    /**
     * Called when an explosion is about to occur, which is cancelled if the event returns true.
     */
    @FunctionalInterface
    public interface Start {
        boolean start(Level level, Explosion explosion);
    }

    /**
     * Called when an explosion has lists of affected blocks and entities, which can be changed.
     */
    @FunctionalInterface
    public interface Detonate {
        void detonate(Level level, Explosion explosion, List<Entity> entityList);
    }
}
