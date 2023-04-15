package gg.moonflower.pollen.impl.render.geometry;

import dev.architectury.registry.ReloadListenerRegistry;
import gg.moonflower.pinwheel.api.geometry.GeometryModel;
import gg.moonflower.pinwheel.api.texture.ModelTexture;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryAtlasTexture;
import gg.moonflower.pollen.api.render.util.v1.BackgroundLoader;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
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
import java.util.stream.Stream;

@ApiStatus.Internal
public final class GeometryModelManagerImpl {

    private static final Logger LOGGER = LogManager.getLogger();
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

    public static GeometryAtlasTexture getAtlas() {
        return atlas;
    }

    private static class Reloader implements PreparableReloadListener {
        @Override
        public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
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
    }

    private static class Atlas extends TextureAtlasHolder implements GeometryAtlasTexture {

        private final Set<ModelTexture> textures;

        public Atlas(TextureManager textureManager, ResourceLocation resourceLocation, String string) {
            super(textureManager, resourceLocation, string);
        }

        @Override
        protected Stream<ResourceLocation> getResourcesToLoad() {
            return null;
        }

        @Override
        public ResourceLocation getAtlasLocation() {
            return null;
        }

        @Override
        public TextureAtlasSprite getSprite(ResourceLocation location) {
            return null;
        }
    }
}
