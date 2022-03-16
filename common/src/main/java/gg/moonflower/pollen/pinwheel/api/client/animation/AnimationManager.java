package gg.moonflower.pollen.pinwheel.api.client.animation;

import gg.moonflower.pollen.api.registry.resource.PollinatedPreparableReloadListener;
import gg.moonflower.pollen.api.registry.resource.ResourceRegistry;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.pinwheel.api.common.animation.AnimationData;
import gg.moonflower.pollen.pinwheel.api.common.util.BackgroundLoader;
import gg.moonflower.pollen.pinwheel.core.client.animation.DeprecatedLocalAnimationLoader;
import gg.moonflower.pollen.pinwheel.core.client.animation.LocalAnimationLoader;
import gg.moonflower.pollen.pinwheel.core.client.util.DynamicReloader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * <p>Manages {@link AnimationData} loading using custom loaders, which can then be accessed through {@link #getAnimation(ResourceLocation)}.</p>
 *
 * @author Ocelot
 * @see BackgroundLoader
 * @since 1.0.0
 */
public final class AnimationManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Reloader RELOADER = new Reloader();
    private static final DynamicReloader DYNAMIC_RELOADER = new DynamicReloader();
    private static final Set<BackgroundLoader<Map<ResourceLocation, AnimationData>>> LOADERS = new HashSet<>();
    private static final Map<ResourceLocation, AnimationData> ANIMATIONS = new HashMap<>();

    static {
        DYNAMIC_RELOADER.addListener(RELOADER);
    }

    private AnimationManager() {
    }

    @ApiStatus.Internal
    public static void init() {
        ResourceRegistry.registerReloadListener(PackType.CLIENT_RESOURCES, RELOADER);
        addLoader(new DeprecatedLocalAnimationLoader()); // TODO remove in 2.0.0
        addLoader(new LocalAnimationLoader());
    }

    /**
     * Adds the specified animation loader.
     *
     * @param loader The loader to add
     */
    public static void addLoader(BackgroundLoader<Map<ResourceLocation, AnimationData>> loader) {
        LOADERS.add(loader);
    }

    /**
     * <p>Reloads all animations and opens the loading gui if specified.</p>
     *
     * @param showLoadingScreen Whether to show the loading screen during the reload
     * @return A future for when the reload is complete
     */
    public static CompletableFuture<?> reload(boolean showLoadingScreen) {
        return DYNAMIC_RELOADER.reload(showLoadingScreen);
    }

    /**
     * Fetches an animation by the specified name.
     *
     * @param location The name of the model
     * @return The animation found or {@link AnimationData#EMPTY} if there was no animation
     */
    public static AnimationData getAnimation(ResourceLocation location) {
        return ANIMATIONS.computeIfAbsent(location, key ->
        {
            LOGGER.warn("Unknown animation with key '{}'", location);
            return AnimationData.EMPTY;
        });
    }

    /**
     * @return Whether a reload is currently happening
     */
    public static boolean isReloading() {
        return DYNAMIC_RELOADER.isReloading();
    }

    private static class Reloader implements PollinatedPreparableReloadListener {
        @Override
        public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            Map<ResourceLocation, AnimationData> animationData = new HashMap<>();
            return CompletableFuture.allOf(LOADERS.stream().map(animationLoader -> animationLoader.reload(resourceManager, backgroundExecutor, gameExecutor).thenAcceptAsync(pairs ->
            {
                for (Map.Entry<ResourceLocation, AnimationData> entry : pairs.entrySet())
                    if (animationData.put(entry.getKey(), entry.getValue()) != null)
                        LOGGER.warn("Duplicate animation: " + entry.getKey());
            }, gameExecutor)).toArray(CompletableFuture[]::new)).thenCompose(stage::wait).thenRunAsync(() ->
            {
                LOGGER.info("Loaded " + animationData.size() + " animations.");
                ANIMATIONS.clear();
                ANIMATIONS.putAll(animationData);
            }, gameExecutor);
        }

        @Override
        public ResourceLocation getPollenId() {
            return new ResourceLocation(Pollen.MOD_ID, "animation_manager");
        }
    }
}
