package gg.moonflower.pollen.api.event.events.entity;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * Events for modifying entities when they join or leave a world.
 *
 * @author abigailfails
 * @since 1.0.0
 */
public final class EntityEvents {

    public static final PollinatedEvent<Join> JOIN = EventRegistry.create(Join.class, events -> (entity, level) -> {
        for (Join event : events)
            if (!event.onJoin(entity, level))
                return false;
        return true;
    });
    public static final PollinatedEvent<Leave> LEAVE = EventRegistry.createLoop(Leave.class);

    private EntityEvents() {
    }

    /**
     * Called when an entity joins the world. It is not added if the event returns true.
     */
    @FunctionalInterface
    public interface Join {
        boolean onJoin(Entity entity, Level level);
    }

    /**
     * Called when an entity leaves the world.
     */
    @FunctionalInterface
    public interface Leave {
        void onLeave(Entity entity, Level level);
    }
}
