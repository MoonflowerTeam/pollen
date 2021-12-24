package gg.moonflower.pollen.api.registry.resource;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class SimplePollinatedPreparableReloadListener<T> implements PreparableReloadListener {

    public final CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
        return CompletableFuture.supplyAsync(() -> this.prepare(resourceManager, profilerFiller), executor).thenCompose(preparationBarrier::wait).thenAcceptAsync((object) -> this.apply(object, resourceManager, profilerFiller2), executor2);
    }

    protected abstract T prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller);

    protected abstract void apply(T object, ResourceManager resourceManager, ProfilerFiller profilerFiller);
}
