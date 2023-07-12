package gg.moonflower.pollen.api.render.geometry.v1;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

/**
 * Fetches geometry texture sprites by the specified name.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface GeometryAtlasTexture {

    /**
     * @return The texture location of this atlas
     */
    ResourceLocation getAtlasLocation();

    /**
     * Fetches a texture for the specified location.
     *
     * @param location The location of the texture to grab
     * @return The sprite with that key or the missing texture sprite
     */
    TextureAtlasSprite getSprite(ResourceLocation location);
}
