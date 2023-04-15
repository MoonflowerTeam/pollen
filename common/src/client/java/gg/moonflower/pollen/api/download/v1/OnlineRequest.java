package gg.moonflower.pollen.api.download.v1;

import gg.moonflower.pollen.impl.base.OnlineRequestImpl;
import gg.moonflower.pollen.impl.download.OnlineRequestImpl;
import net.minecraft.util.HttpUtil;

import java.io.IOException;
import java.io.InputStream;
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
public interface OnlineRequest {

    /**
     * <p>Fetches data from the specified url.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param url The url to get the data from
     * @return An open stream to the internet
     */
    static InputStream get(String url) throws IOException {
        return OnlineRequestImpl.get(url);
    }

    /**
     * <p>Fetches data from the specified url on the specified executor.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param url      The url to get the data from
     * @param executor The executor to run the request on
     * @return A copy of the data read from the specified URL
     */
    static CompletableFuture<InputStream> request(String url, Executor executor) {
        return OnlineRequestImpl.request(url, executor);
    }

    /**
     * <p>Fetches data from the specified url.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param url The url to get the data from
     * @return A copy of the data read from the specified URL
     */
    static CompletableFuture<InputStream> request(String url) {
        return request(url, HttpUtil.DOWNLOAD_EXECUTOR);
    }

    /**
     * Sets the user agent to use when making online requests.
     *
     * @param userAgent The new user agent
     */
    static void setUserAgent(String userAgent) {
        OnlineRequestImpl.setUserAgent(userAgent);
    }
}
