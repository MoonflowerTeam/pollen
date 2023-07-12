package gg.moonflower.pollen.impl.render.geometry;

import gg.moonflower.pinwheel.api.texture.ModelTexture;
import gg.moonflower.pinwheel.api.texture.TextureTable;
import gg.moonflower.pollen.api.download.v1.FileCache;
import gg.moonflower.pollen.api.pinwheelbridge.v1.PinwheelBridge;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryAtlasTexture;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class GeometryTextureSpriteUploader extends SimplePreparableReloadListener<TextureAtlas.Preparations> implements GeometryAtlasTexture, AutoCloseable {

    public static final ResourceLocation ATLAS_LOCATION = new ResourceLocation(Pollen.MOD_ID, "textures/atlas/geometry.png");
    private static final Pattern FROM_LOCATION = Pattern.compile("_");
    private static final Logger LOGGER = LogManager.getLogger();
    private final TextureAtlas textureAtlas;
    private final Set<ModelTexture> textures;

    public GeometryTextureSpriteUploader(TextureManager textureManager) {
        this.textureAtlas = new TextureAtlas(ATLAS_LOCATION);
        this.textures = new HashSet<>();
        textureManager.register(this.textureAtlas.location(), this.textureAtlas);
    }

    private static String encodeUrl(String url) {
        return "base32" + new Base32().encodeToString(url.getBytes(StandardCharsets.UTF_8)).toLowerCase(Locale.ROOT).replaceAll("=","_");
    }

    @Override
    public ResourceLocation getAtlasLocation() {
        return ATLAS_LOCATION;
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location) {
        return this.textureAtlas.getSprite(location);
    }

    @Override
    protected TextureAtlas.Preparations prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        try (OnlineRepository onlineRepository = new OnlineRepository()) {
            profiler.startTick();
            profiler.push("stitching");
            ResourceManager onlineResources = new OnlineResourceManager(resourceManager, onlineRepository, this.textures.stream().filter(texture -> texture.type() == ModelTexture.Type.ONLINE).collect(Collectors.toSet()));
            TextureAtlas.Preparations sheetData = this.textureAtlas.prepareToStitch(onlineResources, this.textures.stream().filter(texture -> texture.type() != ModelTexture.Type.UNKNOWN).map(texture -> PinwheelBridge.toLocation(texture.location())).distinct(), profiler, Minecraft.getInstance().options.mipmapLevels().get());
            profiler.pop();
            profiler.endTick();
            return sheetData;
        }
    }

    @Override
    protected void apply(TextureAtlas.Preparations sheetData, ResourceManager resourceManager, ProfilerFiller profiler) {
        profiler.startTick();
        profiler.push("upload");
        this.textureAtlas.reload(sheetData);
        profiler.pop();
        profiler.endTick();
    }

    @Override
    public void close() {
        this.textureAtlas.clearTextureData();
    }

    public GeometryTextureSpriteUploader setTextures(Map<ResourceLocation, TextureTable> textures) {
        this.textures.clear();
        this.textures.addAll(textures.values().stream().flatMap(table -> table.getTextureDefinitions().values().stream().flatMap(Arrays::stream)).collect(Collectors.toSet()));
        return this;
    }

    private static class OnlineResourceManager implements ResourceManager {

        private static final String PREFIX = "textures/";
        private static final String SUFFIX = ".png";

        private final ResourceManager parent;
        private final OnlineRepository repository;
        private final Map<String, Pair<CompletableFuture<Path>, CompletableFuture<ResourceMetadata>>> onlineLocations;

        private OnlineResourceManager(ResourceManager parent, OnlineRepository repository, Collection<ModelTexture> onlineTextures) {
            this.parent = parent;
            this.repository = repository;
            this.onlineLocations = onlineTextures.stream().map(ModelTexture::data).distinct().collect(Collectors.toMap(url -> url, this::updateCache));
        }

        @Nullable
        private static String decodeUrl(ResourceLocation location) {
            String[] parts = location.getPath().substring(PREFIX.length(), location.getPath().length() - SUFFIX.length()).split("/");
            if (parts[parts.length - 1].startsWith("base32")) {
                return new String(new Base32().decode(FROM_LOCATION.matcher(parts[parts.length - 1].substring(6).toUpperCase(Locale.ROOT)).replaceAll("=")));
            }
            return null;
        }

        private static InputStream read(CompletableFuture<Path> pathFuture) throws IOException {
            try {
                Path path = pathFuture.get();
                if (path == null) {
                    throw new FileNotFoundException();
                }
                return new FileInputStream(path.toFile());
            } catch (InterruptedException | ExecutionException e) {
                throw new IOException(e);
            }
        }

        private Pair<CompletableFuture<Path>, CompletableFuture<ResourceMetadata>> updateCache(String url) {
            String metadataUrl;
            String extension = FilenameUtils.getExtension(url);
            String[] urlParts = url.split("." + extension);
            if (urlParts.length <= 1) {
                metadataUrl = url + ".mcmeta";
            } else {
                metadataUrl = urlParts[0] + extension + ".mcmeta" + urlParts[1];
            }

            CompletableFuture<Path> texturePath = this.repository.requestResource(url, false);
            CompletableFuture<ResourceMetadata> metadataPath = this.repository.requestResource(metadataUrl, true).thenApplyAsync(path ->
            {
                try (InputStream stream = read(CompletableFuture.completedFuture(path))) {
                    return ResourceMetadata.fromJsonStream(stream);
                } catch (Exception ignored) {
                    return ResourceMetadata.EMPTY;
                }
            }, Util.ioPool());
            return Pair.of(texturePath, metadataPath);
        }

        private Optional<Resource> getOnlineResource(ResourceLocation location) {
            String url = decodeUrl(location);
            if (url != null && this.onlineLocations.containsKey(url)) {
                return Optional.of(this.createResource(url));
            }
            return Optional.empty();
        }

        private Resource createResource(String url) {
            Pair<CompletableFuture<Path>, CompletableFuture<ResourceMetadata>> files = this.onlineLocations.get(url);
            return new Resource("online", () -> read(files.getLeft()), () -> files.getRight().join());
        }

        @Override
        public Set<String> getNamespaces() {
            return this.parent.getNamespaces();
        }

        @Override
        public List<Resource> getResourceStack(ResourceLocation location) {
            List<Resource> resources = this.parent.getResourceStack(location);
            Optional<Resource> online = this.getOnlineResource(location);
            return online.map(resource -> Stream.concat(resources.stream(), Stream.of(resource)).toList()).orElse(resources);
        }

        @Override
        public Map<ResourceLocation, Resource> listResources(String prefix, Predicate<ResourceLocation> filter) {
            Map<ResourceLocation, Resource> resources = this.parent.listResources(prefix, filter);
            if (PREFIX.startsWith(prefix)) {
                for (String url : this.onlineLocations.keySet()) {
                    ResourceLocation location = new ResourceLocation("online", PREFIX + encodeUrl(url) + SUFFIX);
                    if (filter.test(location)) {
                        resources.put(location, this.createResource(url));
                    }
                }
            }
            return resources;
        }

        @Override
        public Map<ResourceLocation, List<Resource>> listResourceStacks(String prefix, Predicate<ResourceLocation> filter) {
            Map<ResourceLocation, List<Resource>> resources = this.parent.listResourceStacks(prefix, filter);
            if (PREFIX.startsWith(prefix)) {
                for (String url : this.onlineLocations.keySet()) {
                    ResourceLocation location = new ResourceLocation("online", PREFIX + encodeUrl(url) + SUFFIX);
                    if (filter.test(location)) {
                        resources.put(location, List.of(this.createResource(url)));
                    }
                }
            }
            return resources;
        }

        @Override
        public Optional<Resource> getResource(ResourceLocation location) {
            return this.getOnlineResource(location).or(() -> this.parent.getResource(location));
        }

        @Override
        public Stream<PackResources> listPacks() {
            return this.parent.listPacks();
        }
    }

    private static class OnlineRepository implements AutoCloseable {

        private final ExecutorService executor;
        private final FileCache cache;
        private final Map<String, CompletableFuture<Path>> resources;

        private OnlineRepository() {
            this.executor = FileCache.createOnlineWorker();
            this.cache = FileCache.timed(this.executor, 1, TimeUnit.DAYS);
            this.resources = new HashMap<>();
        }

        public CompletableFuture<Path> requestResource(String url, boolean ignoreMissing) {
            return this.resources.computeIfAbsent(url, key -> this.cache.requestResource(url, ignoreMissing));
        }

        @Override
        public void close() {
            this.executor.shutdown();
            try {
                if (!this.executor.awaitTermination(10, TimeUnit.SECONDS))
                    LOGGER.warn("Took more than 10 seconds to terminate online worker");
            } catch (Exception e) {
                LOGGER.error("Failed to terminate online worker", e);
            }
        }
    }
}