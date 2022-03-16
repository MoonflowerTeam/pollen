package gg.moonflower.pollen.pinwheel.api.client.geometry;

import gg.moonflower.pollen.api.registry.resource.PollinatedPreparableReloadListener;
import gg.moonflower.pollen.api.registry.resource.ResourceRegistry;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.pinwheel.api.common.animation.AnimationData;
import gg.moonflower.pollen.pinwheel.api.common.util.BackgroundLoader;
import gg.moonflower.pollen.pinwheel.core.client.geometry.DeprecatedLocalGeometryModelLoader;
import gg.moonflower.pollen.pinwheel.core.client.geometry.LocalGeometryModelLoader;
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
 * <p>Manages {@link AnimationData} loading using custom loaders, which can then be accessed through {@link #getModel(ResourceLocation)}.</p>
 *
 * @author Ocelot
 * @see BackgroundLoader
 * @since 1.0.0
 */
public final class GeometryModelManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Reloader RELOADER = new Reloader();
    private static final DynamicReloader DYNAMIC_RELOADER = new DynamicReloader();
    private static final Set<BackgroundLoader<Map<ResourceLocation, GeometryModel>>> LOADERS = new HashSet<>();
    private static final Map<ResourceLocation, GeometryModel> MODELS = new HashMap<>();

    static {
        DYNAMIC_RELOADER.addListener(RELOADER);
    }

    @ApiStatus.Internal
    public static void init() {
        ResourceRegistry.registerReloadListener(PackType.CLIENT_RESOURCES, RELOADER);
        addLoader(new DeprecatedLocalGeometryModelLoader()); // TODO remove in 2.0.0
        addLoader(new LocalGeometryModelLoader());
    }

    /**
     * Adds the specified animation loader.
     *
     * @param loader The loader to add
     */
    public static void addLoader(BackgroundLoader<Map<ResourceLocation, GeometryModel>> loader) {
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
     * @return The bedrock model found or {@link GeometryModel#EMPTY} if there was no model
     */
    public static GeometryModel getModel(ResourceLocation location) {
        return MODELS.computeIfAbsent(location, key ->
        {
            LOGGER.warn("Unknown geometry model with key '{}'", location);
            return GeometryModel.EMPTY;
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
            Map<ResourceLocation, GeometryModel> geometryModels = new HashMap<>();
            return CompletableFuture.allOf(LOADERS.stream().map(modelLoader -> modelLoader.reload(resourceManager, backgroundExecutor, gameExecutor).thenAcceptAsync(pairs ->
            {
                for (Map.Entry<ResourceLocation, GeometryModel> entry : pairs.entrySet())
                    if (geometryModels.put(entry.getKey(), entry.getValue()) != null)
                        LOGGER.warn("Duplicate geometry model: " + entry.getKey());
            }, gameExecutor)).toArray(CompletableFuture[]::new)).thenCompose(stage::wait).thenRunAsync(() ->
            {
                LOGGER.info("Loaded " + geometryModels.size() + " geometry models.");
                MODELS.clear();
                MODELS.putAll(geometryModels);
            }, gameExecutor);
        }

        @Override
        public ResourceLocation getPollenId() {
            return new ResourceLocation(Pollen.MOD_ID, "geometry_model_manager");
        }
    }
}
