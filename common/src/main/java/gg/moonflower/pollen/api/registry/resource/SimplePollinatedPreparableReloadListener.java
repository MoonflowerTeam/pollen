package gg.moonflower.pollen.api.registry.resource;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

// TODO remove in 2.0.0
/**
 * @deprecated This is not necessary. Extending {@link SimplePreparableReloadListener} and implementing {@link PollinatedPreparableReloadListener} is enough.
 * @param <T> The type of object to pass between threads
 */
public abstract class SimplePollinatedPreparableReloadListener<T> extends SimplePreparableReloadListener<T> implements PollinatedPreparableReloadListener {
}
