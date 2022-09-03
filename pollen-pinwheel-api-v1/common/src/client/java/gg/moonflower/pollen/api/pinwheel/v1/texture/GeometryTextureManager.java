package gg.moonflower.pollen.api.pinwheel.v1.texture;

import com.mojang.logging.LogUtils;
import gg.moonflower.pollen.impl.pinwheel.DynamicReloader;
import gg.moonflower.pollen.impl.pinwheel.PinwheelApiInitializer;
import gg.moonflower.pollen.impl.pinwheel.texture.GeometryTextureSpriteUploader;
import gg.moonflower.pollen.impl.pinwheel.texture.LocalTextureTableLoader;
import gg.moonflower.pollen.impl.pinwheel.texture.StaticTextureTableLoader;
import gg.moonflower.pollen.api.pinwheel.v1.geometry.GeometryModelRenderer;
import gg.moonflower.pollen.api.registry.resource.PollinatedPreparableReloadListener;
import gg.moonflower.pollen.api.registry.resource.ResourceRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Manages textures for all geometry models. Used by {@link GeometryModelRenderer}.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class GeometryTextureManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Reloader RELOADER = new Reloader();
    private static final DynamicReloader DYNAMIC_RELOADER = new DynamicReloader();
    private static final Set<TextureTableLoader> PROVIDERS = new HashSet<>();
    private static final Map<ResourceLocation, GeometryModelTextureTable> TEXTURES = new HashMap<>();
    private static GeometryTextureSpriteUploader spriteUploader;

    static {
        DYNAMIC_RELOADER.addListener(RELOADER);
    }

    private GeometryTextureManager() {
    }

    @ApiStatus.Internal
    public static void init() {
        ResourceRegistry.registerReloadListener(PackType.CLIENT_RESOURCES, RELOADER);
        addProvider(new LocalTextureTableLoader());
    }

    /**
     * Adds the specified texture under the specified location. This will not ever change or be unloaded.
     *
     * @param location  The location to upload under
     * @param texture   The texture table to load
     * @param hashTable The table to load hashes from or <code>null</code> for no hashes
     */
    public static void addTexture(ResourceLocation location, GeometryModelTextureTable texture, @Nullable String hashTable) {
        addProvider(new StaticTextureTableLoader(location, texture, hashTable));
    }

    /**
     * Adds the specified provider to the reloading task. Textures are reloaded at user discretion.
     *
     * @param provider The provider for textures
     */
    public static void addProvider(TextureTableLoader provider) {
        PROVIDERS.add(provider);
    }

    /**
     * Fetches a texture table by the specified location.
     *
     * @param location The location of the texture table
     * @return The texture table with the name or {@link GeometryModelTextureTable#EMPTY} if there was no texture
     */
    public static GeometryModelTextureTable getTextures(ResourceLocation location) {
        return TEXTURES.computeIfAbsent(location, key ->
        {
            LOGGER.warn("Unknown texture table with key '{}'", location);
            return GeometryModelTextureTable.EMPTY;
        });
    }

    /**
     * <p>Reloads all textures and opens the loading gui if specified.</p>
     *
     * @param showLoadingScreen Whether to show the loading screen during the reload
     * @return A future for when the reload is complete
     */
    public static CompletableFuture<?> reload(boolean showLoadingScreen) {
        return DYNAMIC_RELOADER.reload(showLoadingScreen);
    }

    /**
     * @return The base geometry atlas texture
     */
    public static GeometryAtlasTexture getAtlas() {
        return spriteUploader;
    }

    /**
     * @return A collection of all textures loaded
     */
    public static Collection<GeometryModelTextureTable> getAllTextures() {
        return TEXTURES.values();
    }

    /**
     * @return Whether the texture manager is currently happening
     */
    public static boolean isReloading() {
        return DYNAMIC_RELOADER.isReloading();
    }

    private static class Reloader implements PollinatedPreparableReloadListener {
        @Override
        public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            return CompletableFuture.allOf(PROVIDERS.stream().map(provider -> provider.reload(CompletableFuture::completedFuture, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor)).toArray(CompletableFuture[]::new)).thenApplyAsync(a ->
                    {
                        Map<ResourceLocation, GeometryModelTextureTable> textures = new HashMap<>();
                        Set<String> hashTables = new HashSet<>();
                        PROVIDERS.forEach(provider -> {
                            try {
                                provider.addTextures((location, texture) ->
                                {
                                    if (textures.put(location, texture) != null)
                                        LOGGER.warn("Texture at location '" + location + "' already exists and is being overridden.");
                                });
                            } catch (Exception e) {
                                LOGGER.error("Provider " + provider + " failed to gather textures", e);
                            }
                        });
                        PROVIDERS.forEach(provider -> provider.addHashTables(hashTables::add));
                        return Pair.of(textures, hashTables.toArray(new String[0]));
                    }, backgroundExecutor)
                    .thenComposeAsync(pair -> {
                        if (spriteUploader == null)
                            spriteUploader = new GeometryTextureSpriteUploader(Minecraft.getInstance().getTextureManager());
                        return spriteUploader.setTextures(pair.getLeft(), pair.getRight()).reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
                    }, gameExecutor)
                    .thenCompose(stage::wait).thenAcceptAsync(textures ->
                    {
                        TEXTURES.clear();
                        PROVIDERS.forEach(provider -> provider.addTextures((location, texture) ->
                        {
                            if (TEXTURES.put(location, texture) != null)
                                LOGGER.warn("Texture at location '" + location + "' already exists and is being overridden.");
                        }));
                    }, gameExecutor);
        }

        @Override
        public ResourceLocation getPollenId() {
            return new ResourceLocation(PinwheelApiInitializer.MOD_ID, "geometry_texture_manager");
        }
    }
}
