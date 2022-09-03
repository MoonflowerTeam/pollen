package gg.moonflower.pollen.api.pinwheel.v1.animation;

import com.mojang.logging.LogUtils;
import gg.moonflower.pollen.api.pinwheel.v1.BackgroundLoader;
import gg.moonflower.pollen.api.registry.resource.PollinatedPreparableReloadListener;
import gg.moonflower.pollen.api.registry.resource.ResourceRegistry;
import gg.moonflower.pollen.impl.pinwheel.DynamicReloader;
import gg.moonflower.pollen.impl.pinwheel.PinwheelApiInitializer;
import gg.moonflower.pollen.impl.pinwheel.animation.LocalAnimationLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Manages {@link AnimationData} loading using custom loaders, which can then be accessed through {@link #getAnimation(ResourceLocation)}.
 *
 * @author Ocelot
 * @see BackgroundLoader
 * @since 1.0.0
 */
public final class AnimationManager {

    private static final Logger LOGGER = LogUtils.getLogger();
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
        public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
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
            return new ResourceLocation(PinwheelApiInitializer.MOD_ID, "animation_manager");
        }
    }
}
