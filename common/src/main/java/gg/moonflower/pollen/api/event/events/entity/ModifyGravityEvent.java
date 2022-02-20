package gg.moonflower.pollen.api.event.events.entity;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.LivingEntity;

/**
 * Fired every tick to modify the effects of gravity on living entities.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface ModifyGravityEvent {

    PollinatedEvent<ModifyGravityEvent> EVENT = EventRegistry.create(ModifyGravityEvent.class, events -> (entity, gravity) -> {
        for (ModifyGravityEvent event : events)
            gravity = event.modifyGravity(entity, gravity);
        return gravity;
    });

    /**
     * Modifies gravity for the specified entity.
     *
     * @param entity  The entity to modify
     * @param gravity The new gravity modifier
     * @return The gravity value
     */
    double modifyGravity(LivingEntity entity, double gravity);
}
