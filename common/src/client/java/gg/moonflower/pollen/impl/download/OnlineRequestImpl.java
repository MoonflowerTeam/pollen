package gg.moonflower.pollen.impl.download;

import com.mojang.logging.LogUtils;
import net.minecraft.util.HttpUtil;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public final class OnlineRequestImpl {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    private OnlineRequestImpl() {
    }

    public static InputStream get(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.addRequestProperty("User-Agent", USER_AGENT);
        InputStream stream = connection.getInputStream();

        if (connection.getResponseCode() != 200) {
            IOException exception = new IOException("Failed to connect to '" + url + "'. " + connection.getResponseCode() + " " + connection.getResponseMessage());
            try {
                stream.close();
            } catch (Throwable e) {
                exception.addSuppressed(e);
            }
            throw exception;
        }

        return stream;
    }

    public static CompletableFuture<InputStream> request(String url, Executor executor) {
        return CompletableFuture.supplyAsync(() ->
        {
            try (InputStream stream = get(url)) {
                return IOUtils.toBufferedInputStream(stream);
            } catch (Exception e) {
                LOGGER.error("Failed to fully read stream from '" + url + "'", e);
                return null;
            }
        }, executor);
    }

    public static void setUserAgent(String userAgent) {
        USER_AGENT = userAgent;
    }
}
