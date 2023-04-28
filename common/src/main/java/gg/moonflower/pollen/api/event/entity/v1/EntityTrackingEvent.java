package gg.moonflower.pollen.api.event.entity.v1;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface EntityTrackingEvent {

    Event<EntityTrackingEvent> START_TRACKING = EventFactory.createLoop();

    Event<EntityTrackingEvent> STOP_TRACKING = EventFactory.createLoop();

    void event(Entity trackedEntity, Player player);
}
