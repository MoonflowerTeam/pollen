package gg.moonflower.pollen.api.event.events.registry.client;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.core.event.registry.RegisterAtlasSpriteEventImpl;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * Fired for each {@link net.minecraft.client.renderer.texture.TextureAtlas} to register new sprites.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface RegisterAtlasSpriteEvent {

    /**
     * Retrieves an event for the specified atlas id.
     *
     * @param atlasId The id of the texture to register into
     * @return The event for that specific atlas path.
     */
    static PollinatedEvent<RegisterAtlasSpriteEvent> event(ResourceLocation atlasId) {
        return RegisterAtlasSpriteEventImpl.get(atlasId);
    }

    /**
     * Registers all sprites into the provided atlas.
     *
     * @param atlas    The atlas to register into
     * @param registry The registry to add sprites into the texture
     */
    void registerSprites(TextureAtlas atlas, Consumer<ResourceLocation> registry);
}
