package gg.moonflower.pollen.impl.download;

import gg.moonflower.pollen.api.download.v1.FileCache;
import gg.moonflower.pollen.api.download.v1.OnlineRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class TimedTextureCache implements FileCache {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Executor executor;
    private final long cacheTime;
    private final TimeUnit cacheTimeUnit;

    public TimedTextureCache(Executor executor, long cacheTime, TimeUnit cacheTimeUnit) {
        this.executor = executor;
        this.cacheTime = cacheTime;
        this.cacheTimeUnit = cacheTimeUnit;
    }

    @Override
    public CompletableFuture<Path> requestResource(String url, boolean ignoreMissing) {
        return CompletableFuture.supplyAsync(() ->
        {
            try {
                return GeometryCache.getPath(url, this.cacheTime, this.cacheTimeUnit, s ->
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
                if (!ignoreMissing)
                    LOGGER.error("Failed to fetch resource from '" + url + "'", e);
                return null;
            }
        }, this.executor);
    }
}
