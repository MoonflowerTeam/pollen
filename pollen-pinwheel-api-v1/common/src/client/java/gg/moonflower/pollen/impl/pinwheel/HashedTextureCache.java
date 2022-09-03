package gg.moonflower.pollen.impl.pinwheel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import gg.moonflower.pollen.api.pinwheel.v1.FileCache;
import gg.moonflower.pollen.api.pinwheel.v1.geometry.GeometryCache;
import gg.moonflower.pollen.api.util.OnlineRequest;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class HashedTextureCache implements FileCache {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();

    private final Executor executor;
    private final CompletableFuture<Map<String, String>> hashes;

    public HashedTextureCache(Executor executor, String... hashTableUrls) {
        this.executor = executor;

        Map<String, String> hashes = new ConcurrentHashMap<>();
        this.hashes = CompletableFuture.allOf(Arrays.stream(hashTableUrls).map(it -> CompletableFuture.runAsync(() ->
        {
            try (InputStreamReader reader = new InputStreamReader(OnlineRequest.get(it))) {
                hashes.putAll(GSON.fromJson(reader, TypeToken.getParameterized(Map.class, String.class, String.class).getType()));
            } catch (Exception e) {
                LOGGER.error("Failed to load hash table from '" + it + "'");
            }
        }, executor)).toArray(CompletableFuture[]::new)).handleAsync((__, t) ->
        {
            if (t != null) {
                LOGGER.error("Error downloading hashes from: " + String.join(", ", hashTableUrls), t);
                return Collections.emptyMap();
            } else {
                LOGGER.debug("Downloaded " + hashes.size() + " hashes from " + hashTableUrls.length + " hash table(s)");
                return hashes;
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Path> requestResource(String url, boolean ignoreMissing) {
        return this.hashes.thenApplyAsync(hashes ->
        {
            try {
                try {
                    return GeometryCache.getPath(url, hashes.get(url), s ->
                    {
                        try {
                            return OnlineRequest.get(url);
                        } catch (IOException e) {
                            if (!ignoreMissing)
                                LOGGER.error("Failed to read data from '" + url + "'");
                            return null;
                        }
                    });
                } catch (Exception e) {
                    throw new IOException("Failed to load texture data", e);
                }
            } catch (IOException e) {
                if (!ignoreMissing)
                    LOGGER.error("Failed to fetch resource from '" + url + "'", e);
                return null;
            }
        }, this.executor);
    }
}
