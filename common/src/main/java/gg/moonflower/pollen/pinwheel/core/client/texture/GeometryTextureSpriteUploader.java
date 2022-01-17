package gg.moonflower.pollen.pinwheel.core.client.texture;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.pinwheel.api.client.FileCache;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryAtlasTexture;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import gg.moonflower.pollen.pinwheel.core.client.util.HashedTextureCache;
import gg.moonflower.pollen.pinwheel.core.client.util.TimedTextureCache;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class GeometryTextureSpriteUploader extends SimplePreparableReloadListener<TextureAtlas.Preparations> implements GeometryAtlasTexture, AutoCloseable {

    public static final ResourceLocation ATLAS_LOCATION = new ResourceLocation(Pollen.MOD_ID, "textures/atlas/geometry.png");
    private static final Logger LOGGER = LogManager.getLogger();
    private final TextureAtlas textureAtlas;
    private final Set<GeometryModelTexture> textures;
    private String[] hashTables;

    public GeometryTextureSpriteUploader(TextureManager textureManager) {
        this.textureAtlas = new TextureAtlas(ATLAS_LOCATION);
        this.textures = new HashSet<>();
        this.hashTables = new String[0];
        textureManager.register(this.textureAtlas.location(), this.textureAtlas);
    }

    @SuppressWarnings("unused")
    private void beginStitch(long startTime, Stopwatch stopwatch) {
        stopwatch.start();
    }

    private void endStitch(Stopwatch stopwatch) {
        stopwatch.stop();
        LOGGER.debug("Took " + stopwatch + " to process " + this.textures.size() + " geometry textures");
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
        try (OnlineRepository onlineRepository = new OnlineRepository(this.hashTables)) {
            profiler.startTick();
            profiler.push("stitching");
            Stopwatch stopwatch = Stopwatch.createUnstarted();
            this.beginStitch(System.currentTimeMillis(), stopwatch);
            TextureAtlas.Preparations sheetData = this.textureAtlas.prepareToStitch(new OnlineResourceManager(resourceManager, onlineRepository, this.textures.stream().filter(texture -> texture.getType() == GeometryModelTexture.Type.ONLINE).collect(Collectors.toSet())), this.textures.stream().filter(texture -> texture.getType() == GeometryModelTexture.Type.LOCATION || texture.getType() == GeometryModelTexture.Type.ONLINE).map(GeometryModelTexture::getLocation).distinct(), profiler, Minecraft.getInstance().options.mipmapLevels);
            this.endStitch(stopwatch);
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

    public GeometryTextureSpriteUploader setTextures(Map<ResourceLocation, GeometryModelTextureTable> textures, String[] hashTables) {
        this.textures.clear();
        this.textures.addAll(textures.values().stream().flatMap(table -> table.getTextures().stream().flatMap(Arrays::stream)).collect(Collectors.toSet()));
        this.hashTables = hashTables;
        return this;
    }

    private static class OnlineResourceManager implements ResourceManager {

        private final ResourceManager parent;
        private final OnlineRepository repository;
        private final Set<String> uncached;
        private final Map<String, Pair<CompletableFuture<Path>, CompletableFuture<JsonObject>>> onlineLocations;

        private OnlineResourceManager(ResourceManager parent, OnlineRepository repository, Set<GeometryModelTexture> onlineTextures) {
            this.parent = parent;
            this.repository = repository;
            this.uncached = onlineTextures.stream().filter(texture -> !texture.canCache()).map(GeometryModelTexture::getData).collect(Collectors.toSet());
            this.onlineLocations = onlineTextures.stream().map(GeometryModelTexture::getData).distinct().collect(Collectors.toMap(url -> url, this::updateCache));
        }

        @Nullable
        private static String parseUrl(ResourceLocation location) {
            String[] parts = location.getPath().split("/");
            if (parts[parts.length - 1].startsWith("base32"))
                return new String(new Base32().decode(parts[parts.length - 1].substring(6).toUpperCase(Locale.ROOT).replaceAll("_", "=")));
            return null;
        }

        @Nullable
        private static InputStream read(CompletableFuture<Path> pathFuture) {
            try {
                Path path = pathFuture.join();
                return path == null ? null : new FileInputStream(path.toFile());
            } catch (Exception e) {
                LOGGER.error("Took too long to fetch texture data", e);
                return null;
            }
        }

        private Pair<CompletableFuture<Path>, CompletableFuture<JsonObject>> updateCache(String url) {
            String metadataUrl;
            String extension = FilenameUtils.getExtension(url);
            String[] urlParts = url.split("." + extension);
            if (urlParts.length <= 1) {
                metadataUrl = url + ".mcmeta";
            } else {
                metadataUrl = urlParts[0] + extension + ".mcmeta" + urlParts[1];
            }

            CompletableFuture<Path> texturePath = this.repository.requestResource(url, !this.uncached.contains(url), false);
            CompletableFuture<JsonObject> metadataPath = this.repository.requestResource(metadataUrl, false, true).thenApplyAsync(path ->
            {
                InputStream stream = read(CompletableFuture.completedFuture(path));
                if (stream == null)
                    return null;

                try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    return GsonHelper.parse(bufferedreader);
                } catch (Exception ignored) {
                }

                return null;
            }, Util.ioPool());
            return Pair.of(texturePath, metadataPath);
        }

        @Override
        public Set<String> getNamespaces() {
            return this.parent.getNamespaces();
        }

        @Override
        public Resource getResource(ResourceLocation resourceLocation) throws IOException {
            String url = parseUrl(new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath().substring(9, resourceLocation.getPath().length() - 4)));
            if (url != null) {
                if (!this.onlineLocations.containsKey(url))
                    throw new IOException("Failed to fetch texture data from '" + url + "'");

                Pair<CompletableFuture<Path>, CompletableFuture<JsonObject>> files = this.onlineLocations.get(url);
                InputStream textureStream = read(files.getLeft());
                if (textureStream == null)
                    throw new IOException("Failed to fetch texture data from '" + url + "'");

                return new OnlineResource(url, resourceLocation, textureStream, files.getRight().join());
            }
            return this.parent.getResource(resourceLocation);
        }

        @Override
        public boolean hasResource(ResourceLocation resourceLocation) {
            return resourceLocation.getPath().startsWith("base32") || this.parent.hasResource(resourceLocation);
        }

        @Override
        public List<Resource> getResources(ResourceLocation resourceLocation) throws IOException {
            return this.parent.getResources(resourceLocation);
        }

        @Override
        public Collection<ResourceLocation> listResources(String path, Predicate<String> filter) {
            return this.parent.listResources(path, filter);
        }

        @Override
        public Stream<PackResources> listPacks() {
            return this.parent.listPacks();
        }

        private static class OnlineResource implements Resource {

            private final String url;
            private final ResourceLocation textureLocation;
            private final InputStream stream;
            private final JsonObject metadataJson;

            private OnlineResource(String url, ResourceLocation textureLocation, InputStream stream, @Nullable JsonObject metadataJson) {
                this.url = url;
                this.textureLocation = textureLocation;
                this.stream = stream;
                this.metadataJson = metadataJson;
            }

            @Override
            public ResourceLocation getLocation() {
                return textureLocation;
            }

            @Override
            public InputStream getInputStream() {
                return stream;
            }

            @Override
            public boolean hasMetadata() {
                return this.metadataJson != null;
            }

            @Nullable
            @Override
            public <T> T getMetadata(MetadataSectionSerializer<T> serializer) {
                if (this.metadataJson == null)
                    return null;
                String s = serializer.getMetadataSectionName();
                return this.metadataJson.has(s) ? serializer.fromJson(GsonHelper.getAsJsonObject(this.metadataJson, s)) : null;
            }

            @Override
            public String getSourceName() {
                return Pollen.MOD_ID + "_online";
            }

            @Override
            public void close() throws IOException {
                this.stream.close();
            }

            @Override
            public String toString() {
                return "OnlineResource{" +
                        "url='" + url + '\'' +
                        ", textureLocation=" + textureLocation +
                        '}';
            }
        }
    }

    private static class OnlineRepository implements AutoCloseable {

        private final ExecutorService executor;
        private final FileCache hashedCache;
        private final FileCache cache;
        private final Map<String, CompletableFuture<Path>> resources;

        private OnlineRepository(String[] hashTableUrls) {
            AtomicInteger idGenerator = new AtomicInteger();
            this.executor = createOnlineWorker(idGenerator::getAndIncrement);
            this.hashedCache = new HashedTextureCache(this.executor, hashTableUrls);
            this.cache = new TimedTextureCache(this.executor, 1, TimeUnit.DAYS);
            this.resources = new HashMap<>();
        }

        private static ExecutorService createOnlineWorker(Supplier<Integer> idGenerator) {
            int i = Mth.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
            ExecutorService executorservice;
            if (i <= 0) {
                executorservice = MoreExecutors.newDirectExecutorService();
            } else {
                executorservice = new ForkJoinPool(i, pool ->
                {
                    ForkJoinWorkerThread forkjoinworkerthread = new ForkJoinWorkerThread(pool) {
                        @Override
                        protected void onTermination(@Nullable Throwable t) {
                            if (t != null) {
                                LOGGER.warn("{} died", this.getName(), t);
                            } else {
                                LOGGER.debug("{} shutdown", this.getName());
                            }

                            super.onTermination(t);
                        }
                    };
                    forkjoinworkerthread.setName("Worker-Geometry Online Fetcher-" + idGenerator.get());
                    return forkjoinworkerthread;
                }, (thread, throwable) ->
                {
                    if (throwable instanceof CompletionException)
                        throwable = throwable.getCause();

                    if (throwable instanceof ReportedException) {
                        Bootstrap.realStdoutPrintln(((ReportedException) throwable).getReport().getFriendlyReport());
                        System.exit(-1);
                    }

                    LOGGER.error("Caught exception in thread " + thread, throwable);
                }, true);
            }

            return executorservice;
        }

        public CompletableFuture<Path> requestResource(String url, boolean cache, boolean ignoreMissing) {
            return this.resources.computeIfAbsent(url, key -> cache ? this.hashedCache.requestResource(url, ignoreMissing) : this.cache.requestResource(url, ignoreMissing));
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