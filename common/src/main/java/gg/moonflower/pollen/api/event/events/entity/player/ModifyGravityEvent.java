package gg.moonflower.pollen.api.event.events.entity.player;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.LivingEntity;

/**
 * Modifies the effects of gravity on living entities.
 *
 * @author Ocelot
 * @since 1.0.0
 */
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
     * @return The modified gravity
     */
    double modifyGravity(LivingEntity entity, double gravity);
}
