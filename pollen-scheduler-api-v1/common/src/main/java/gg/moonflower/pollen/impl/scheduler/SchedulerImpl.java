package gg.moonflower.pollen.impl.scheduler;

import gg.moonflower.pollen.api.base.platform.Platform;
import gg.moonflower.pollen.api.scheduler.v1.Scheduler;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class SchedulerImpl implements Scheduler {

    private static final Map<Boolean, SchedulerImpl> SIDED_SCHEDULERS = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            SIDED_SCHEDULERS.values().forEach(SchedulerImpl::shutdownInternal);
            SIDED_SCHEDULERS.clear();
        }));
        // TODO
//        ServerLifecycleEvents.STOPPING.register(server -> {
//            SchedulerImpl scheduler = SIDED_SCHEDULERS.remove(false);
//            if (scheduler != null)
//                scheduler.shutdownInternal();
//        });
    }

    private final Executor serverExecutor;
    private final ScheduledExecutorService service;

    private SchedulerImpl(boolean client) {
        this.serverExecutor = Platform.getGameExecutor();
        this.service = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, (client ? "Client" : "Server") + " Scheduler"));
    }

    public static Scheduler get(boolean client) {
        return SIDED_SCHEDULERS.computeIfAbsent(client, SchedulerImpl::new);
    }

    private void shutdownInternal() {
        this.service.shutdown();
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
