package gg.moonflower.pollen.api.event;

import org.jetbrains.annotations.ApiStatus;

/**
 * An abstract event that can be fired using {@link #invoker()}. Based on Fabric's event API.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@ApiStatus.NonExtendable
public abstract class PollinatedEvent<T> {

    protected volatile T invoker;

    /**
     * @return The callback for invoking all listeners of this event
     */
    public T invoker() {
        return invoker;
    }

    /**
     * Register a listener to happen in this event.
     *
     * @param listener The listener to add
     */
    public abstract void register(T listener);
}
