package gg.moonflower.pollen.api.event.entity.v1;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface EntityTrackingEvent {

    Event<EntityTrackingEvent> START_TRACKING = EventFactory.createLoop();

    Event<EntityTrackingEvent> STOP_TRACKING = EventFactory.createLoop();

    void event(Entity trackedEntity, ServerPlayer player);
}
