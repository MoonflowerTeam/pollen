package gg.moonflower.pollen.pinwheel.api.client.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

/**
 * <p>Fetches a geometry texture by the specified name.</p>
 *
 * @author Ocelot
 * @since 1.0.0
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
