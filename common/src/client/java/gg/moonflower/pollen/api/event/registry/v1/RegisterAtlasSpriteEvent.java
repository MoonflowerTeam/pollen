package gg.moonflower.pollen.api.event.registry.v1;

import dev.architectury.event.Event;
import gg.moonflower.pollen.impl.event.registry.RegisterAtlasSpriteEventImpl;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * Fired for each {@link TextureAtlas} to register new sprites.
 *
 * @author Ocelot
 * @since 2.0.0
 */
@FunctionalInterface
public interface RegisterAtlasSpriteEvent {

    /**
     * Retrieves an event for the specified atlas id.
     *
     * @param atlasId The id of the texture to register into
     * @return The event for that specific atlas path.
     */
    static Event<RegisterAtlasSpriteEvent> event(ResourceLocation atlasId) {
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
