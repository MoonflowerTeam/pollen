package gg.moonflower.pollen.api.event.config.v1;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import gg.moonflower.pollen.api.config.v1.PollinatedModConfig;

/**
 * Fired for config data changes.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface ConfigEvent {

    Event<ConfigEvent> LOADING = EventFactory.createLoop();

    Event<ConfigEvent> RELOADING = EventFactory.createLoop();

    /**
     * @param config The config that was updated
     */
    void configChanged(PollinatedModConfig config);
}
