package gg.moonflower.pollen.core.event.registry;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.event.events.registry.client.RegisterAtlasSpriteEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class RegisterAtlasSpriteEventImpl {

    private static final Map<ResourceLocation, PollinatedEvent<RegisterAtlasSpriteEvent>> EVENTS = new ConcurrentHashMap<>();

    public static PollinatedEvent<RegisterAtlasSpriteEvent> get(ResourceLocation key) {
        return EVENTS.computeIfAbsent(key, __ -> createEvent());
    }

    private static PollinatedEvent<RegisterAtlasSpriteEvent> createEvent() {
        return EventRegistry.createLoop(RegisterAtlasSpriteEvent.class);
    }
}
