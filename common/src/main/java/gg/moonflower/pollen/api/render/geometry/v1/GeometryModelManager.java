package gg.moonflower.pollen.api.render.geometry.v1;

import gg.moonflower.pinwheel.api.geometry.GeometryModel;
import gg.moonflower.pollen.api.render.util.v1.BackgroundLoader;
import gg.moonflower.pollen.impl.render.geometry.GeometryModelManagerImpl;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Manages {@link GeometryModel} loading using custom loaders, which can then be accessed through {@link #getModel(ResourceLocation)}.
 *
 * @author Ocelot
 * @see BackgroundLoader
 * @since 2.0.0
 */
public interface GeometryModelManager {

    /**
     * Adds the specified animation loader.
     *
     * @param loader The loader to add
     */
    static void addLoader(BackgroundLoader<Map<ResourceLocation, GeometryModel>> loader) {
        GeometryModelManagerImpl.addLoader(loader);
    }

    /**
     * Fetches an animation by the specified name.
     *
     * @param location The name of the model
     * @return The bedrock model found or {@link GeometryModel#EMPTY} if there was no model
     */
    static GeometryModel getModel(ResourceLocation location) {
        return GeometryModelManagerImpl.getModel(location);
    }
}
