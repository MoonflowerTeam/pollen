package gg.moonflower.pollen.api.util;

import net.minecraft.util.HttpUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * An asynchronous way to make requests to the internet.
 * <p>{@link #get(String)} can be used to open a new stream to the internet. <b><i>NOTE: THIS STREAM CANNOT BE KEPT OPEN AND IS NOT OFF-THREAD!</i></b>
 * <p>{@link #request(String)} and {@link #request(String, Executor)} can be used instead to fetch all data on another thread.
 *
 * @author Ocelot
 * @see CompletableFuture
 * @since 1.0.0
 */
public final class OnlineRequest {

    private static final Logger LOGGER = LogManager.getLogger();
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    private OnlineRequest() {
    }

    /**
     * <p>Fetches data from the specified url.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param url The url to get the data from
     * @return An open stream to the internet
     */
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

    /**
     * <p>Fetches data from the specified url on the specified executor.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param url      The url to get the data from
     * @param executor The executor to run the request on
     * @return A copy of the data read from the specified URL
     */
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

    /**
     * <p>Fetches data from the specified url.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param url The url to get the data from
     * @return A copy of the data read from the specified URL
     */
    public static CompletableFuture<InputStream> request(String url) {
        return request(url, HttpUtil.DOWNLOAD_EXECUTOR);
    }

    /**
     * Sets the user agent to use when making online requests.
     *
     * @param userAgent The new user agent
     */
    public static void setUserAgent(String userAgent) {
        USER_AGENT = userAgent;
    }
}
