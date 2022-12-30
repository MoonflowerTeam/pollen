package gg.moonflower.pollen.api.events.v1.client.resource;

import gg.moonflower.pollen.api.base.event.EventRegistry;
import gg.moonflower.pollen.api.base.event.PollinatedEvent;

/**
 * Fired each time the client receives tag collections.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface ClientTagUpdateEvent {

    PollinatedEvent<ClientTagUpdateEvent> EVENT = EventRegistry.createLoop(ClientTagUpdateEvent.class);

    /**
     * Called when tags reload.
     */
    void onTagsReloaded();
}
