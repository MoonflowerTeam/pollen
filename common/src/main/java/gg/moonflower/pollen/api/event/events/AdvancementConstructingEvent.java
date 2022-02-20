package gg.moonflower.pollen.api.event.events;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;

/**
 * Fired for each new advancement deserialized from JSON.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface AdvancementConstructingEvent {

    PollinatedEvent<AdvancementConstructingEvent> EVENT = EventRegistry.create(AdvancementConstructingEvent.class, events -> (builder, context) -> {
        for (AdvancementConstructingEvent event : events)
            event.modifyAdvancement(builder, context);
    });

    /**
     * Called each time an advancement is deserialized.
     *
     * @param builder The builder for the advancement. Modify the builder just before the advancement is deserialized from JSON
     * @param context The context for deserialization
     */
    void modifyAdvancement(Advancement.Builder builder, DeserializationContext context);
}
