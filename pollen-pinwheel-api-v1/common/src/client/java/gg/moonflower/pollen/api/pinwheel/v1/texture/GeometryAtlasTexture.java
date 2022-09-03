package gg.moonflower.pollen.api.pinwheel.v1.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

/**
 * An instance of the geometry atlas for retrieving sprites and the atlas location.
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
