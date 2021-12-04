package gg.moonflower.pollen.api.event.events.client.render;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;

/**
 * Called just after {@link com.mojang.blaze3d.systems.RenderSystem#initRenderer} when it's safe to do raw OpenGL initialization code.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface InitRendererEvent {

    PollinatedEvent<InitRendererEvent> EVENT = EventRegistry.createLoop(InitRendererEvent.class);

    /**
     * Sets up any initial rendering pipeline.
     */
    void initRenderer();
}
