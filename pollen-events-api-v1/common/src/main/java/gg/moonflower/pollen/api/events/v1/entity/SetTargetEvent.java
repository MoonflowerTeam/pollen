package gg.moonflower.pollen.api.events.v1.entity;

import gg.moonflower.pollen.api.base.event.EventRegistry;
import gg.moonflower.pollen.api.base.event.PollinatedEvent;
import net.minecraft.world.entity.LivingEntity;

/**
 * Fired each time a mob changes their attack target.
 *
 * @author abigailfails
 * @since 1.0.0
 */
@FunctionalInterface
public interface SetTargetEvent {

    PollinatedEvent<SetTargetEvent> EVENT = EventRegistry.createLoop(SetTargetEvent.class);

    /**
     * Called when the specified attacker sets the target to the specified target.
     *
     * @param attacker The attacking entity
     * @param target   The target entity
     */
    void setTarget(LivingEntity attacker, LivingEntity target);
}
