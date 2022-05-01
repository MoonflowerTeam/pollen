package gg.moonflower.pollen.api.registry.resource;

import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

// TODO remove in 2.0.0
/**
 * @deprecated This is not necessary. Extending {@link SimplePreparableReloadListener} and implementing {@link PollinatedPreparableReloadListener} is enough.
 * @param <T> The type of object to pass between threads
 */
@Deprecated
public abstract class SimplePollinatedPreparableReloadListener<T> extends SimplePreparableReloadListener<T> implements PollinatedPreparableReloadListener {
}
