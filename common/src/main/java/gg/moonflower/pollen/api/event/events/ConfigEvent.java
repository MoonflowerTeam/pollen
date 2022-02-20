package gg.moonflower.pollen.api.event.events;

import gg.moonflower.pollen.api.config.PollinatedModConfig;
import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;

/**
 * Fired for config data changes.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface ConfigEvent {

    PollinatedEvent<ConfigEvent> LOADING = EventRegistry.create(ConfigEvent.class, events -> config -> {
        for (ConfigEvent event : events)
            event.configChanged(config);
    });

    PollinatedEvent<ConfigEvent> RELOADING = EventRegistry.create(ConfigEvent.class, events -> (config) -> {
        for (ConfigEvent event : events)
            event.configChanged(config);
    });

    /**
     * @param config The config that was updated
     */
    void configChanged(PollinatedModConfig config);
}
