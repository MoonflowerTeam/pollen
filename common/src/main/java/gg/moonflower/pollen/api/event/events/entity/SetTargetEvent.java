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
     * Fired when an entity targets another entity.
     *
     * @param attacker The attacking entity
     * @param target   The target entity
     */
    void setTarget(LivingEntity attacker, LivingEntity target);
}
