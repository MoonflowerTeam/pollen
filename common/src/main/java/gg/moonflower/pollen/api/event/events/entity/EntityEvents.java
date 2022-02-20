package gg.moonflower.pollen.api.event.events.entity;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

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
     * Fired when an entity joins the level.
     *
     * @author abigailfails
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Join {

        /**
         * Called when the specified entity is trying to spawn into the specified level.
         *
         * @param entity The entity to spawn
         * @param level  The level to add the entity to
         * @return <code>true</code> to prevent the entity from being added
         */
        boolean onJoin(Entity entity, Level level);
    }

    /**
     * Fired when an entity leaves the level.
     *
     * @author abigailfails
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Leave {


        /**
         * Called when the specified entity has left the specified level.
         *
         * @param entity The entity to spawn
         * @param level  The level to add the entity to
         */
        void onLeave(Entity entity, Level level);
    }
}
