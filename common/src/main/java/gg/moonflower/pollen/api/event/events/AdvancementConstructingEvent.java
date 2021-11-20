package gg.moonflower.pollen.api.event.events;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;

/**
 * Called for each new advancement deserialized from JSON.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class AdvancementConstructingEvent extends PollinatedEvent {

    private final Advancement.Builder builder;
    private final DeserializationContext context;

    public AdvancementConstructingEvent(Advancement.Builder builder, DeserializationContext context) {
        this.builder = builder;
        this.context = context;
    }

    /**
     * @return The builder for the advancement. Modify the builder just before the advancement is deserialized from JSON
     */
    public Advancement.Builder getBuilder() {
        return builder;
    }

    /**
     * @return The context for deserialization
     */
    public DeserializationContext getContext() {
        return context;
    }
}
