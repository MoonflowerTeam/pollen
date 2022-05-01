package gg.moonflower.pollen.pinwheel.api.client;

import com.google.common.util.concurrent.MoreExecutors;
import gg.moonflower.pollen.pinwheel.core.client.util.HashedTextureCache;
import gg.moonflower.pollen.pinwheel.core.client.util.TimedTextureCache;
import net.minecraft.ReportedException;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.Mth;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.EofSensorInputStream;
import org.apache.http.conn.EofSensorWatcher;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ocelot
 */
public interface FileCache {

    String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
    @ApiStatus.Internal
    AtomicInteger ID_GENERATOR = new AtomicInteger();

    /**
     * Opens a GET stream to the specified URL.
     *
     * @param url The url to open a stream to
     * @return The opened stream to the resource
     * @throws IOException If any error occurs while trying to fetch resources
     */
    static InputStream get(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).build();
        CloseableHttpResponse response = client.execute(get);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() != 200) {
            client.close();
            response.close();
            throw new IOException("Failed to connect to '" + url + "'. " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
        }
        return new EofSensorInputStream(response.getEntity().getContent(), new EofSensorWatcher() {
            @Override
            public boolean eofDetected(InputStream wrapped) {
                return true;
            }

            @Override
            public boolean streamClosed(InputStream wrapped) throws IOException {
                response.close();
                return true;
            }

            @Override
            public boolean streamAbort(InputStream wrapped) throws IOException {
                response.close();
                return true;
            }
        });
    }

    /**
     * @return A new executor intended to be used for downloading a lot of small files
     */
    static ExecutorService createOnlineWorker() {
        Logger logger = LogManager.getLogger();
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
                            logger.warn("{} died", this.getName(), t);
                        } else {
                            logger.debug("{} shutdown", this.getName());
                        }

                        super.onTermination(t);
                    }
                };
                forkjoinworkerthread.setName("Worker-Pollen Online Fetcher-" + ID_GENERATOR.getAndIncrement());
                return forkjoinworkerthread;
            }, (thread, throwable) ->
            {
                if (throwable instanceof CompletionException)
                    throwable = throwable.getCause();

                if (throwable instanceof ReportedException) {
                    Bootstrap.realStdoutPrintln(((ReportedException) throwable).getReport().getFriendlyReport());
                    System.exit(-1);
                }

                logger.error("Caught exception in thread " + thread, throwable);
            }, true);
        }

        return executorservice;
    }

    /**
     * Creates a new {@link FileCache} that does not cache at all.
     *
     * @param executor The executor to download files with
     * @return A new cache
     */
    static FileCache direct(Executor executor) {
        return new TimedTextureCache(executor, 0, TimeUnit.SECONDS);
    }

    /**
     * Creates a new {@link FileCache} that updates based on the md5 hash of the file.
     *
     * @param executor      The executor to download files with
     * @param hashTableUrls The URLs to get hash tables from
     * @return A new cache
     */
    static FileCache hashed(Executor executor, String... hashTableUrls) {
        return new HashedTextureCache(executor, hashTableUrls);
    }

    /**
     * Creates a new {@link FileCache} that updates based on the amount of time that has passed.
     *
     * @param executor      The executor to download files with
     * @param cacheTime     The amount of time to wait before re-downloading files
     * @param cacheTimeUnit The unit of time for cacheTime
     * @return A new cache
     */
    static FileCache timed(Executor executor, long cacheTime, TimeUnit cacheTimeUnit) {
        return new TimedTextureCache(executor, cacheTime, cacheTimeUnit);
    }

    /**
     * Requests an online resource from the specified URL.
     *
     * @param url           The url to download the resource from
     * @param ignoreMissing Whether to print an error when an exception is thrown
     * @return A future for the location of the resource locally
     */
    CompletableFuture<Path> requestResource(String url, boolean ignoreMissing);
}
