package gg.moonflower.pollen.core.client.loader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.client.entitlement.EntitlementManager;
import gg.moonflower.pollen.core.client.entitlement.ModelEntitlement;
import gg.moonflower.pollen.pinwheel.api.client.FileCache;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModel;
import gg.moonflower.pollen.pinwheel.api.common.geometry.GeometryModelData;
import gg.moonflower.pollen.pinwheel.api.common.geometry.GeometryModelParser;
import gg.moonflower.pollen.pinwheel.api.common.util.BackgroundLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Ocelot
 */
public class CosmeticModelLoader implements BackgroundLoader<Map<ResourceLocation, GeometryModel>> {

    private static final Logger LOGGER = LogManager.getLogger();

    public CosmeticModelLoader() {
    }

    private static CompletableFuture<JsonObject> loadModel(FileCache cache, String modelUrl) {
        return cache.requestResource(modelUrl, false).thenApply(path ->
        {
            try {
                if (path == null) {
                    if (!Platform.isProduction())
                        LOGGER.warn("Cosmetic model at '" + modelUrl + "' could not be found");
                    return null;
                }
                try (InputStreamReader reader = new InputStreamReader(new FileInputStream(path.toFile()))) {
                    return JsonParser.parseReader(reader).getAsJsonObject();
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load cosmetic model from '" + modelUrl + "'", e);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Map<ResourceLocation, GeometryModel>> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        ExecutorService executor = FileCache.createOnlineWorker();
        FileCache cache = FileCache.timed(executor, 1, TimeUnit.DAYS);
        Map<ResourceLocation, GeometryModel> models = new ConcurrentHashMap<>();

        return CompletableFuture.allOf(EntitlementManager.getAllEntitlements().filter(entitlement -> entitlement instanceof ModelEntitlement).flatMap(entitlement -> Arrays.stream(((ModelEntitlement) entitlement).getModelUrls())).distinct().map(url -> loadModel(cache, url).thenAcceptAsync(json ->
        {
            if (json == null)
                return;

            try {
                for (GeometryModelData model : GeometryModelParser.parseModel(json)) {
                    ResourceLocation id = new ResourceLocation(Pollen.MOD_ID, model.getDescription().getIdentifier());
                    if (models.put(id, GeometryModel.create(model)) != null)
                        LOGGER.warn("Duplicate geometry model with id: " + id);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to parse cosmetic model: " + json, e);
            }
        }, gameExecutor)).toArray(CompletableFuture[]::new)).thenApplyAsync(__ ->
        {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS))
                    LOGGER.warn("Took more than 10 seconds to terminate online worker");
            } catch (Exception e) {
                LOGGER.error("Failed to terminate online worker", e);
            }
            return models;
        }, gameExecutor);
    }
}
