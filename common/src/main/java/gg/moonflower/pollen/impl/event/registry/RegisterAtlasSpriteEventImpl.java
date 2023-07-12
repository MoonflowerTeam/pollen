package gg.moonflower.pollen.impl.event.registry;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import gg.moonflower.pollen.api.event.registry.v1.RegisterAtlasSpriteEvent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class RegisterAtlasSpriteEventImpl {

    private static final Map<ResourceLocation, Event<RegisterAtlasSpriteEvent>> EVENTS = new ConcurrentHashMap<>();

    public static Event<RegisterAtlasSpriteEvent> get(ResourceLocation key) {
        return EVENTS.computeIfAbsent(key, __ -> createEvent());
    }

    private static Event<RegisterAtlasSpriteEvent> createEvent() {
        return EventFactory.createLoop(RegisterAtlasSpriteEvent.class);
    }
}
