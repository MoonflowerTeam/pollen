package gg.moonflower.pollen.impl.render.particle;

import gg.moonflower.pinwheel.api.particle.ParticleData;
import gg.moonflower.pinwheel.api.particle.ParticleParser;
import gg.moonflower.pollen.api.render.util.v1.BackgroundLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class LocalParticleLoader implements BackgroundLoader<Map<ResourceLocation, ParticleData>> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String FOLDER = "pinwheel/particles";

    @Override
    public CompletableFuture<Map<ResourceLocation, ParticleData>> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, ParticleData> particleData = new HashMap<>();
            try {
                for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(FOLDER, resourceLocation -> resourceLocation.getPath().endsWith(".json")).entrySet()) {
                    ResourceLocation particleLocation = entry.getKey();

                    try (BufferedReader reader = entry.getValue().openAsReader()) {
                        ParticleData particle = ParticleParser.parseParticle(reader);
                        ResourceLocation id = new ResourceLocation(particle.description().identifier());
                        if (particleData.put(id, particle) != null) {
                            LOGGER.warn("Duplicate particle: " + id);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed to load particle: " + particleLocation.getNamespace() + ":" + particleLocation.getPath().substring(FOLDER.length() + 1, particleLocation.getPath().length() - 5), e);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load particles from: " + FOLDER, e);
            }
            return particleData;
        }, backgroundExecutor);
    }
}
