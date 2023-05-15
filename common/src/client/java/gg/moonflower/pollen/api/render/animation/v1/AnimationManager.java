package gg.moonflower.pollen.api.render.animation.v1;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pollen.api.render.util.v1.BackgroundLoader;
import gg.moonflower.pollen.impl.render.animation.AnimationManagerImpl;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Manages {@link AnimationData} loading using custom loaders, which can then be accessed through {@link #getAnimation(ResourceLocation)}.
 *
 * @author Ocelot
 * @see BackgroundLoader
 * @since 2.0.0
 */
public interface AnimationManager {

    /**
     * Adds the specified animation loader.
     *
     * @param loader The loader to add
     */
    static void addLoader(BackgroundLoader<Map<ResourceLocation, AnimationData>> loader) {
        AnimationManagerImpl.addLoader(loader);
    }

    /**
     * Fetches an animation by the specified name.
     *
     * @param location The name of the animation
     * @return The bedrock model found or {@link AnimationData#EMPTY} if there was no animation
     */
    static AnimationData getAnimation(ResourceLocation location) {
        return AnimationManagerImpl.getAnimation(location);
    }
}
