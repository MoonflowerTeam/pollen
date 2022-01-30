package gg.moonflower.pollen.api.event.events.client.resource;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;

/**
 * Fired each time the client receives tag collections.
 *
 * @since 1.0.0
 */
public interface ClientTagUpdateEvent {

    PollinatedEvent<ClientTagUpdateEvent> EVENT = EventRegistry.createLoop(ClientTagUpdateEvent.class);

    /**
     * Called when tags reload.
     */
    void onTagsReloaded();
}
