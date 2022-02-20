package gg.moonflower.pollen.api.event.events.client.render;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;

/**
 * Called each time chunks are refreshed, usually by F3+A or changing resource packs.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface ReloadRendersEvent {

    PollinatedEvent<ReloadRendersEvent> EVENT = EventRegistry.create(ReloadRendersEvent.class, events -> () -> {
        for (ReloadRendersEvent event : events)
            event.reloadRenders();
    });

    /**
     * Called when renderers reload.
     */
    void reloadRenders();
}
