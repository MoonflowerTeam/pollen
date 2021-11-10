package gg.moonflower.pollen.pinwheel.api.common.util;

import net.minecraft.server.packs.resources.ResourceManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * <p>Loads data in the background.</p>
 *
 * @param <T> The type of data to load
 * @author Ocelot
 * @since 1.0.0
 */
public interface BackgroundLoader<T> {
    /**
     * Reloads all data.
     *
     * @param resourceManager    The resource manager currently being used
     * @param backgroundExecutor The background executor for async tasks
     * @param gameExecutor       The game executor for tasks that are thread-sensitive
     * @return A future of data that will be present in the future
     */
    CompletableFuture<T> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor);
}
