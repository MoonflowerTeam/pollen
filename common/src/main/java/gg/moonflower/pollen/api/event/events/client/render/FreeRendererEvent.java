package gg.moonflower.pollen.api.event.events.client.render;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.client.Minecraft;

/**
 * Called in {@link Minecraft#close()} when resources are destroyed.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface FreeRendererEvent {

    PollinatedEvent<FreeRendererEvent> EVENT = EventRegistry.createLoop(FreeRendererEvent.class);

    /**
     * Closes any native resources.
     */
    void closeRenderer();
}
