package gg.moonflower.pollen.impl.render.geometry.texture;

import com.google.common.base.Suppliers;
import dev.architectury.registry.ReloadListenerRegistry;
import gg.moonflower.pinwheel.api.texture.TextureTable;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryAtlasTexture;
import gg.moonflower.pollen.api.render.util.v1.BackgroundLoader;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.impl.render.geometry.GeometryTextureSpriteUploader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class GeometryTextureManagerImpl {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Reloader RELOADER = new Reloader();
    private static final Set<BackgroundLoader<Map<ResourceLocation, TextureTable>>> LOADERS = new HashSet<>();
    private static final Map<ResourceLocation, TextureTable> TEXTURES = new HashMap<>();
    private static final Supplier<StaticTextureTableLoader> STATIC_LOADER = Suppliers.memoize(() -> {
        StaticTextureTableLoader loader = new StaticTextureTableLoader();
        addProvider(loader);
        return loader;
    });
    private static GeometryTextureSpriteUploader spriteUploader;

    private GeometryTextureManagerImpl() {
    }

    public static void init() {
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, RELOADER, new ResourceLocation(Pollen.MOD_ID, "geometry_texture_manager"));
        addProvider(new LocalTextureTableLoader());
    }

    public static void addTexture(ResourceLocation location, TextureTable texture) {
        STATIC_LOADER.get().addTexture(location, texture);
    }

    public static void addProvider(BackgroundLoader<Map<ResourceLocation, TextureTable>> loader) {
        LOADERS.add(loader);
    }

    public static TextureTable getTextures(ResourceLocation location) {
        return TEXTURES.computeIfAbsent(location, key -> {
            LOGGER.warn("Unknown texture table with key '{}'", location);
            return TextureTable.EMPTY;
        });
    }

    public static GeometryAtlasTexture getAtlas() {
        return spriteUploader;
    }

    public static Collection<TextureTable> getAllTextures() {
        return TEXTURES.values();
    }

    private static class Reloader implements PreparableReloadListener {
        @Override
        public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            Map<ResourceLocation, TextureTable> textures = new HashMap<>();
            return CompletableFuture.allOf(LOADERS.stream().map(textureLoader -> textureLoader.reload(resourceManager, backgroundExecutor, gameExecutor).thenAcceptAsync(models -> {
                for (Map.Entry<ResourceLocation, TextureTable> entry : models.entrySet()) {
                    if (textures.put(entry.getKey(), entry.getValue()) != null) {
                        LOGGER.warn("Duplicate model texture: " + entry.getKey());
                    }
                }
            }, gameExecutor)).toArray(CompletableFuture[]::new)).thenComposeAsync(pair -> {
                if (spriteUploader == null) {
                    spriteUploader = new GeometryTextureSpriteUploader(Minecraft.getInstance().getTextureManager());
                }
                return spriteUploader.setTextures(textures).reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }, gameExecutor).thenCompose(stage::wait).thenRunAsync(() -> {
                LOGGER.info("Loaded " + textures.size() + " texture tables.");
                TEXTURES.clear();
                TEXTURES.putAll(textures);
            }, gameExecutor);
        }
    }
}
