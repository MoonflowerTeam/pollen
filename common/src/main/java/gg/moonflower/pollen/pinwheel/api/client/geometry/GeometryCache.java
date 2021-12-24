package gg.moonflower.pollen.pinwheel.api.client.geometry;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvents;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Caches online geometry textures by md5 hash.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class GeometryCache {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final Path CACHE_FOLDER = Paths.get(Minecraft.getInstance().gameDirectory.toURI()).resolve(Pollen.MOD_ID + "-cache");
    private static final Object METADATA_LOCK = new Object();
    private static final Object IO_LOCK = new Object();

    private static final Path CACHE_METADATA_LOCATION = CACHE_FOLDER.resolve("cache.json");
    private static final int METADATA_WRITE_TIME = 5000;
    private static volatile JsonObject CACHE_METADATA = new JsonObject();
    private static volatile long nextWriteTime = Long.MAX_VALUE;

    static {
        if (Files.exists(CACHE_METADATA_LOCATION)) {
            LOGGER.debug("Reading cache metadata from file.");
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(CACHE_METADATA_LOCATION.toFile()))) {
                CACHE_METADATA = new JsonParser().parse(reader).getAsJsonObject();
            } catch (Exception e) {
                LOGGER.error("Failed to load cache metadata", e);
            }
        }
        TickEvents.CLIENT_POST.register(() -> {
            if (nextWriteTime == Long.MAX_VALUE)
                return;

            if (System.currentTimeMillis() - nextWriteTime > 0) {
                nextWriteTime = Long.MAX_VALUE;
                Util.backgroundExecutor().execute(GeometryCache::writeMetadata);
            }
        });
    }

    private GeometryCache() {
    }

    private static synchronized void writeMetadata() {
        LOGGER.debug("Writing cache metadata to file.");
        try (FileOutputStream os = new FileOutputStream(CACHE_METADATA_LOCATION.toFile())) {
            if (!Files.exists(CACHE_FOLDER))
                Files.createDirectory(CACHE_FOLDER);
            IOUtils.write(GSON.toJson(CACHE_METADATA), os, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("Failed to write cache metadata", e);
        }
    }

    private static boolean isCached(String url, @Nullable String hash, Path imageFile) {
        if (Files.exists(imageFile)) {
            if (hash == null)
                return true;

            String key = DigestUtils.md5Hex(url);
            if (CACHE_METADATA.has(key) && CACHE_METADATA.get(key).isJsonPrimitive() && CACHE_METADATA.get(key).getAsJsonPrimitive().isString()) {
                if (!hash.equalsIgnoreCase(CACHE_METADATA.get(key).getAsString())) {
                    if (!Platform.isProduction())
                        LOGGER.warn("Hash for '" + url + "' did not match. Expected " + hash + ", got " + CACHE_METADATA.get(key).getAsString());
                    return false;
                }
                return true;
            }
            try (InputStream stream = new FileInputStream(imageFile.toFile())) {
                String fileCache = DigestUtils.md5Hex(stream);
                synchronized (METADATA_LOCK) {
                    CACHE_METADATA.addProperty(key, fileCache);
                    nextWriteTime = System.currentTimeMillis() + METADATA_WRITE_TIME;
                }
                if (hash.equalsIgnoreCase(fileCache))
                    return true;
            } catch (Exception e) {
                LOGGER.error("Failed to read image '" + url + "'", e);
            }
        }
        return false;
    }

    /**
     * Fetches the texture by the specified name.
     *
     * @param url     The name of the texture to fetch
     * @param hash    The hash of the texture
     * @param fetcher The function providing a new stream
     * @return The location of a file that can be opened with the data
     */
    @Nullable
    public static Path getPath(String url, @Nullable String hash, Function<String, InputStream> fetcher) {
        Path imageFile = CACHE_FOLDER.resolve(DigestUtils.md5Hex(url));

        if (isCached(url, hash, imageFile))
            return imageFile;

        InputStream fetchedStream = fetcher.apply(url);
        if (fetchedStream == null) {
            if (hash == null) {
                try {
                    if (!Files.exists(CACHE_FOLDER))
                        synchronized (IO_LOCK) {
                            Files.createDirectory(CACHE_FOLDER);
                        }
                    if (!Files.exists(imageFile))
                        synchronized (IO_LOCK) {
                            Files.createFile(imageFile);
                        }
                } catch (Exception e) {
                    LOGGER.error("Failed to create empty file '" + imageFile + "' for '" + url + "'", e);
                }
            }
            return null;
        }

        try {
            synchronized (IO_LOCK) {
                if (!Files.exists(CACHE_FOLDER))
                    Files.createDirectory(CACHE_FOLDER);
                Files.copy(fetchedStream, imageFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return imageFile;
        } catch (Exception e) {
            LOGGER.error("Failed to write image '" + url + "' to '" + imageFile + "'", e);
        } finally {
            IOUtils.closeQuietly(fetchedStream);
        }

        return null;
    }

    /**
     * Fetches the texture by the specified name.
     *
     * @param url     The name of the texture to fetch
     * @param timeout The amount of time the cache should remain valid
     * @param unit    The unit of time timeout is defined in
     * @param fetcher The function providing a new stream
     * @return The location of a file that can be opened with the data
     */
    @Nullable
    public static Path getPath(String url, long timeout, TimeUnit unit, Function<String, InputStream> fetcher) {
        Path imageFile = CACHE_FOLDER.resolve(DigestUtils.md5Hex(url));

        String key = DigestUtils.md5Hex(url);
        if (isCached(url, null, imageFile)) {
            if (CACHE_METADATA.has(key) && CACHE_METADATA.get(key).isJsonPrimitive() && CACHE_METADATA.get(key).getAsJsonPrimitive().isNumber()) {
                long now = System.currentTimeMillis();
                long expirationDate = CACHE_METADATA.get(key).getAsLong();
                if (expirationDate - now > 0)
                    return imageFile;
            }
        }

        InputStream fetchedStream = fetcher.apply(url);
        if (fetchedStream == null) {
            try {
                if (!Files.exists(CACHE_FOLDER))
                    synchronized (IO_LOCK) {
                        Files.createDirectory(CACHE_FOLDER);
                    }
                if (!Files.exists(imageFile))
                    synchronized (IO_LOCK) {
                        Files.createFile(imageFile);
                    }
                synchronized (METADATA_LOCK) {
                    CACHE_METADATA.addProperty(key, System.currentTimeMillis() + unit.toMillis(timeout));
                    nextWriteTime = System.currentTimeMillis() + METADATA_WRITE_TIME;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to create empty file '" + imageFile + "' for '" + url + "'", e);
            }
            return null;
        }

        try {
            synchronized (IO_LOCK) {
                if (!Files.exists(CACHE_FOLDER))
                    Files.createDirectory(CACHE_FOLDER);
                Files.copy(fetchedStream, imageFile, StandardCopyOption.REPLACE_EXISTING);
            }
            synchronized (METADATA_LOCK) {
                CACHE_METADATA.addProperty(key, System.currentTimeMillis() + unit.toMillis(timeout));
                nextWriteTime = System.currentTimeMillis() + METADATA_WRITE_TIME;
            }
            return imageFile;
        } catch (Exception e) {
            LOGGER.error("Failed to write image '" + url + "'", e);
        } finally {
            IOUtils.closeQuietly(fetchedStream);
        }

        return null;
    }
}
