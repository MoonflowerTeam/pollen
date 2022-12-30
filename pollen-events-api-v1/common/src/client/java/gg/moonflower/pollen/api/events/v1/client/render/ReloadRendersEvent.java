package gg.moonflower.pollen.api.events.v1.client.render;

import gg.moonflower.pollen.api.base.event.EventRegistry;
import gg.moonflower.pollen.api.base.event.PollinatedEvent;

/**
 * Called each time chunks are refreshed, usually by F3+A or changing resource packs.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface ReloadRendersEvent {

    PollinatedEvent<ReloadRendersEvent> EVENT = EventRegistry.createLoop(ReloadRendersEvent.class);

    /**
     * Called when renderers reload.
     */
    void reloadRenders();
}
