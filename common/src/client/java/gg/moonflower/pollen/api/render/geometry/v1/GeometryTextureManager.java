package gg.moonflower.pollen.api.render.geometry.v1;

import gg.moonflower.pinwheel.api.texture.TextureTable;
import gg.moonflower.pollen.api.io.v1.BackgroundLoader;
import gg.moonflower.pollen.impl.render.geometry.texture.GeometryTextureManagerImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Manages textures for all geometry model textures.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface GeometryTextureManager {

    /**
     * Adds the specified texture under the specified location. This will not ever change or be unloaded.
     *
     * @param location The location to upload under
     * @param texture  The texture table to load
     */
    static void addTexture(ResourceLocation location, TextureTable texture) {
        GeometryTextureManagerImpl.addTexture(location, texture);
    }

    /**
     * Adds the specified provider to the reloading task. Textures are reloaded with {@link BackgroundLoader#reload(ResourceManager, Executor, Executor)}.
     *
     * @param loader The loader for texture tables
     */
    static void addProvider(BackgroundLoader<Map<ResourceLocation, TextureTable>> loader) {
        GeometryTextureManagerImpl.addProvider(loader);
    }

    /**
     * Fetches a texture table by the specified location.
     *
     * @param location The location of the texture table
     * @return The texture table with the name or {@link TextureTable#EMPTY} if there was no texture
     */
    static TextureTable getTextures(ResourceLocation location) {
        return GeometryTextureManagerImpl.getTextures(location);
    }

    /**
     * @return The base geometry atlas texture
     */
    static GeometryAtlasTexture getAtlas() {
        return GeometryTextureManagerImpl.getAtlas();
    }

    /**
     * @return A collection of all textures loaded
     */
    static Collection<TextureTable> getAllTextures() {
        return GeometryTextureManagerImpl.getAllTextures();
    }
}
