package gg.moonflower.pollen.impl.render.particle;

import dev.architectury.registry.ReloadListenerRegistry;
import gg.moonflower.pinwheel.api.particle.ParticleData;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleManager;
import gg.moonflower.pollen.api.render.util.v1.BackgroundLoader;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
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

@ApiStatus.Internal
public final class BedrockParticleManagerImpl {

    private static final Logger LOGGER = LogManager.getLogger(BedrockParticleManager.class);
    private static final Reloader RELOADER = new Reloader();
    private static final Set<BackgroundLoader<Map<ResourceLocation, ParticleData>>> LOADERS = new HashSet<>();
    private static final Map<ResourceLocation, ParticleData> PARTICLES = new HashMap<>();

    private BedrockParticleManagerImpl() {
    }

    public static void init() {
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, RELOADER, new ResourceLocation(Pollen.MOD_ID, "custom_particle_manager"));
        addLoader(new LocalParticleLoader());
    }

    public static void addLoader(BackgroundLoader<Map<ResourceLocation, ParticleData>> loader) {
        LOADERS.add(loader);
    }

    public static ParticleData getParticle(ResourceLocation location) {
        return PARTICLES.computeIfAbsent(location, key -> {
            LOGGER.warn("Unknown particle with key '{}'", location);
            return ParticleData.EMPTY;
        });
    }

    public static boolean hasParticle(ResourceLocation location) {
        return PARTICLES.containsKey(location) && PARTICLES.get(location) != ParticleData.EMPTY;
    }

    private static class Reloader implements PreparableReloadListener {
        @Override
        public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            Map<ResourceLocation, ParticleData> particleData = new HashMap<>();
            return CompletableFuture.allOf(LOADERS.stream().map(loader -> loader.reload(resourceManager, backgroundExecutor, gameExecutor).thenAcceptAsync(pairs -> {
                for (Map.Entry<ResourceLocation, ParticleData> entry : pairs.entrySet()) {
                    if (particleData.put(entry.getKey(), entry.getValue()) != null) {
                        LOGGER.warn("Duplicate particle: " + entry.getKey());
                    }
                }
            }, gameExecutor)).toArray(CompletableFuture[]::new)).thenCompose(stage::wait).thenRunAsync(() -> {
                LOGGER.info("Loaded " + particleData.size() + " particles.");
                PARTICLES.clear();
                PARTICLES.putAll(particleData);
            }, gameExecutor);
        }
    }
}
