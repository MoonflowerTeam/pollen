package gg.moonflower.pollen.core.event;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * Implementation of {@link PollinatedEvent} based on Fabric API.
 *
 * @author Ocelot
 * @see <a href=https://github.com/FabricMC/fabric/blob/1.16/fabric-api-base/src/main/java/net/fabricmc/fabric/impl/base/event/ArrayBackedEvent.java>ArrayBackedEvent</a>
 * @since 1.0.0
 */
@ApiStatus.Internal
public class PollinatedEventImpl<T> extends PollinatedEvent<T> {

    private final Function<T[], T> factory;
    private final Lock lock = new ReentrantLock();
    private T[] handlers;

    @SuppressWarnings("unchecked")
    public PollinatedEventImpl(Class<? super T> type, Function<T[], T> factory) {
        this.factory = factory;
        this.handlers = (T[]) Array.newInstance(type, 0);
        this.invoker = this.factory.apply(this.handlers);
    }

    @Override
    public void register(T listener) {
        Objects.requireNonNull(listener, "Tried to register a null listener");

        this.lock.lock();
        try {
            this.handlers = Arrays.copyOf(this.handlers, this.handlers.length + 1); // Expands the array by 1 and inserts the listener into it
            this.handlers[this.handlers.length - 1] = listener;
            this.invoker = this.factory.apply(this.handlers);
        } finally {
            this.lock.unlock();
        }
    }
}
