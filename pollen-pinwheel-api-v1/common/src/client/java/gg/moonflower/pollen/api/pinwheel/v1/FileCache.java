package gg.moonflower.pollen.api.pinwheel.v1;

import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.logging.LogUtils;
import gg.moonflower.pollen.impl.pinwheel.HashedTextureCache;
import gg.moonflower.pollen.impl.pinwheel.TimedTextureCache;
import net.minecraft.ReportedException;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ocelot
 */
public interface FileCache {

    @ApiStatus.Internal
    AtomicInteger ID_GENERATOR = new AtomicInteger();

    /**
     * @return A new executor intended to be used for downloading a lot of small files
     */
    static ExecutorService createOnlineWorker() {
        Logger logger = LogUtils.getLogger();
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
