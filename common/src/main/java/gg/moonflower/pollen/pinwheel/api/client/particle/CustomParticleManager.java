package gg.moonflower.pollen.pinwheel.api.client.particle;

import gg.moonflower.pollen.api.registry.resource.PollinatedPreparableReloadListener;
import gg.moonflower.pollen.api.registry.resource.ResourceRegistry;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.pinwheel.api.common.particle.ParticleData;
import gg.moonflower.pollen.pinwheel.api.common.util.BackgroundLoader;
import gg.moonflower.pollen.pinwheel.core.client.particle.LocalParticleLoader;
import gg.moonflower.pollen.pinwheel.core.client.util.DynamicReloader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
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
 * Manages {@link ParticleData} loading using custom loaders, which can then be accessed through {@link #getParticle(ResourceLocation)}.
 *
 * @author Ocelot
 * @see BackgroundLoader
 * @since 1.0.0
 */
public final class CustomParticleManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Reloader RELOADER = new Reloader();
    private static final DynamicReloader DYNAMIC_RELOADER = new DynamicReloader();
    private static final Set<BackgroundLoader<Map<ResourceLocation, ParticleData>>> LOADERS = new HashSet<>();
    private static final Map<ResourceLocation, ParticleData> PARTICLES = new HashMap<>();

    static {
        DYNAMIC_RELOADER.addListener(RELOADER);
    }

    private CustomParticleManager() {
    }

    @ApiStatus.Internal
    public static void init() {
        ResourceRegistry.registerReloadListener(PackType.CLIENT_RESOURCES, RELOADER);
        addLoader(new LocalParticleLoader());
    }

    /**
     * Adds the specified particle loader.
     *
     * @param loader The loader to add
     */
    public static void addLoader(BackgroundLoader<Map<ResourceLocation, ParticleData>> loader) {
        LOADERS.add(loader);
    }

    /**
     * Reloads all particles and opens the loading gui if specified.
     *
     * @param showLoadingScreen Whether to show the loading screen during the reload
     * @return A future for when the reload is complete
     */
    public static CompletableFuture<?> reload(boolean showLoadingScreen) {
        return DYNAMIC_RELOADER.reload(showLoadingScreen);
    }

    /**
     * Fetches a particle by the specified name.
     *
     * @param location The name of the particle
     * @return The particle found or {@link ParticleData#EMPTY} if there was no particle
     */
    public static ParticleData getParticle(ResourceLocation location) {
        return PARTICLES.computeIfAbsent(location, key ->
        {
            LOGGER.warn("Unknown particle with key '{}'", location);
            return ParticleData.EMPTY;
        });
    }

    /**
     * Checks if a particle is registered under the specified name.
     *
     * @param location The name of the particle
     * @return Whether that particles exists
     */
    public static boolean hasParticle(ResourceLocation location) {
        return PARTICLES.containsKey(location) && PARTICLES.get(location) != ParticleData.EMPTY;
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
            Map<ResourceLocation, ParticleData> particleData = new HashMap<>();
            return CompletableFuture.allOf(LOADERS.stream().map(loader -> loader.reload(resourceManager, backgroundExecutor, gameExecutor).thenAcceptAsync(pairs ->
            {
                for (Map.Entry<ResourceLocation, ParticleData> entry : pairs.entrySet())
                    if (particleData.put(entry.getKey(), entry.getValue()) != null)
                        LOGGER.warn("Duplicate particle: " + entry.getKey());
            }, gameExecutor)).toArray(CompletableFuture[]::new)).thenCompose(stage::wait).thenRunAsync(() ->
            {
                LOGGER.info("Loaded " + particleData.size() + " particles.");
                PARTICLES.clear();
                PARTICLES.putAll(particleData);
            }, gameExecutor);
        }

        @Override
        public ResourceLocation getPollenId() {
            return new ResourceLocation(Pollen.MOD_ID, "custom_particle_manager");
        }
    }
}
