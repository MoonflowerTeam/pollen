package gg.moonflower.pollen.api.util;

import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvents;
import gg.moonflower.pollen.api.platform.Platform;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Automatically queues tasks into the main loop executor from a {@link ScheduledExecutorService}.
 * <p>As the scheduler is automatically shut down when it is no longer able to be used, manually trying to shut it down is unsupported.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class Scheduler implements ScheduledExecutorService {

    private static final Map<Boolean, Scheduler> SIDED_SCHEDULERS = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            SIDED_SCHEDULERS.values().forEach(Scheduler::shutdownInternal);
            SIDED_SCHEDULERS.clear();
        }));
        ServerLifecycleEvents.STOPPING.register(server -> {
            if (SIDED_SCHEDULERS.containsKey(false))
                SIDED_SCHEDULERS.get(false).onServerStopped();
        });
    }

    private final boolean client;
    private final Executor serverExecutor;
    private final ScheduledExecutorService service;

    private Scheduler(boolean client) {
        this.client = client;
        this.serverExecutor = Platform.getGameExecutor();
        this.service = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, (client ? "Client" : "Server") + " Scheduler"));
    }

    /**
     * Retrieves the scheduler for the specified side.
     *
     * @param client Whether to retrieve the client scheduler
     * @return The scheduler for that side
     */
    public static ScheduledExecutorService get(boolean client) {
        return SIDED_SCHEDULERS.computeIfAbsent(client, Scheduler::new);
    }

    private void shutdownInternal() {
        this.service.shutdown();
    }

    private void onServerStopped() {
        this.shutdownInternal();
        SIDED_SCHEDULERS.remove(this.client);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return this.service.schedule(() -> this.serverExecutor.execute(command), delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return this.service.schedule(callable, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.service.scheduleAtFixedRate(() -> this.serverExecutor.execute(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return this.service.scheduleWithFixedDelay(() -> this.serverExecutor.execute(command), initialDelay, delay, unit);
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("Cannot shut down sided scheduler.");
    }

    @Override
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException("Cannot shut down sided scheduler.");
    }

    @Override
    public boolean isShutdown() {
        return this.service.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.service.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.service.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.service.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.service.submit(() -> this.serverExecutor.execute(task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.service.submit(() -> this.serverExecutor.execute(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.service.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.service.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.service.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.service.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        this.service.execute(() -> this.execute(command));
    }
}
