package gg.moonflower.pollen.api.event.events.entity;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.LivingEntity;

/**
 * Allows you to change the target of entity when it is set.
 *
 * @author abigailfails
 * @since 1.0.0
 */
@FunctionalInterface
public interface SetTargetEvent {

    PollinatedEvent<SetTargetEvent> EVENT = EventRegistry.createLoop(SetTargetEvent.class);

    /**
     * Called when an entity targets another entity.
     */
    void setTarget(LivingEntity attacker, LivingEntity target);
}
