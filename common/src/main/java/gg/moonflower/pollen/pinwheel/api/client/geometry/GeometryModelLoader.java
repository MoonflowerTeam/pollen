package gg.moonflower.pollen.pinwheel.api.client.geometry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * <p>Loads geometry models from different sources.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface GeometryModelLoader {
    /**
     * Reloads all geometry models.
     *
     * @param resourceManager    The resource manager currently being used
     * @param backgroundExecutor The background executor for async tasks
     * @param gameExecutor       The game executor for tasks that are thread-sensitive
     * @return A future of animations that will be present in the future
     */
    CompletableFuture<Map<ResourceLocation, GeometryModel>> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor);
}
