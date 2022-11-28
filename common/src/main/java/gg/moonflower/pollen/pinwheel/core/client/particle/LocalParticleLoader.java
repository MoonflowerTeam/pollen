package gg.moonflower.pollen.pinwheel.core.client.particle;

import gg.moonflower.pollen.pinwheel.api.common.particle.ParticleData;
import gg.moonflower.pollen.pinwheel.api.common.particle.ParticleParser;
import gg.moonflower.pollen.pinwheel.api.common.util.BackgroundLoader;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.InputStreamReader;
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

    private final String folder;

    public LocalParticleLoader() {
        this.folder = "pinwheel/particles";
    }

    @Override
    public CompletableFuture<Map<ResourceLocation, ParticleData>> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() ->
        {
            Map<ResourceLocation, ParticleData> particleData = new HashMap<>();
            try {
                for (ResourceLocation particleLocation : resourceManager.listResources(this.folder, name -> name.endsWith(".json"))) {
                    try (Resource resource = resourceManager.getResource(particleLocation)) {
                        ParticleData particle = ParticleParser.parseParticle(new InputStreamReader(resource.getInputStream()));
                        ResourceLocation id = new ResourceLocation(particle.description().identifier());
                        if (particleData.put(id, particle) != null)
                            LOGGER.warn("Duplicate particle: " + id);
                    } catch (Exception e) {
                        LOGGER.error("Failed to load particle: " + particleLocation.getNamespace() + ":" + particleLocation.getPath().substring(this.folder.length() + 1, particleLocation.getPath().length() - 5), e);
                    }
                }
            } catch (ResourceLocationException e) {
                LOGGER.error("Failed to load particles from: " + this.folder, e);
            }
            return particleData;
        }, backgroundExecutor);
    }
}
