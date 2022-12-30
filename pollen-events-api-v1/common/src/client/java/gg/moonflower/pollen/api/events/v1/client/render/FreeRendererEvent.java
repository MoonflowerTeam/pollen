package gg.moonflower.pollen.api.events.v1.client.render;

import gg.moonflower.pollen.api.base.event.EventRegistry;
import gg.moonflower.pollen.api.base.event.PollinatedEvent;
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
