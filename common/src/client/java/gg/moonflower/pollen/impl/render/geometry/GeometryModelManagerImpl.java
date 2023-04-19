package gg.moonflower.pollen.impl.render.geometry;

import dev.architectury.registry.ReloadListenerRegistry;
import gg.moonflower.pinwheel.api.geometry.GeometryModel;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryModelManager;
import gg.moonflower.pollen.api.io.v1.BackgroundLoader;
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
public final class GeometryModelManagerImpl {

    private static final Logger LOGGER = LogManager.getLogger(GeometryModelManager.class);
    private static final Reloader RELOADER = new Reloader();
    private static final Set<BackgroundLoader<Map<ResourceLocation, GeometryModel>>> LOADERS = new HashSet<>();
    private static final Map<ResourceLocation, GeometryModel> MODELS = new HashMap<>();

    public static void init() {
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, RELOADER, new ResourceLocation(Pollen.MOD_ID, "geometry_model_manager"));
        addLoader(new LocalGeometryModelLoader());
    }

    public static void addLoader(BackgroundLoader<Map<ResourceLocation, GeometryModel>> loader) {
        LOADERS.add(loader);
    }

    public static GeometryModel getModel(ResourceLocation location) {
        return MODELS.computeIfAbsent(location, key ->
        {
            LOGGER.warn("Unknown geometry model with key '{}'", location);
            return GeometryModel.EMPTY;
        });
    }

    private static class Reloader implements PreparableReloadListener {
        @Override
        public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            Map<ResourceLocation, GeometryModel> geometryModels = new HashMap<>();
            return CompletableFuture.allOf(LOADERS.stream().map(modelLoader -> modelLoader.reload(resourceManager, backgroundExecutor, gameExecutor).thenAcceptAsync(models ->
            {
                for (Map.Entry<ResourceLocation, GeometryModel> entry : models.entrySet()) {
                    if (geometryModels.put(entry.getKey(), entry.getValue()) != null) {
                        LOGGER.warn("Duplicate geometry model: " + entry.getKey());
                    }
                }
            }, gameExecutor)).toArray(CompletableFuture[]::new)).thenCompose(stage::wait).thenRunAsync(() ->
            {
                LOGGER.info("Loaded " + geometryModels.size() + " geometry models.");
                MODELS.clear();
                MODELS.putAll(geometryModels);
            }, gameExecutor);
        }
    }
}
