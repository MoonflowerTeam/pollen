package gg.moonflower.pollen.api.registry;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.core.event.PollinatedEventImpl;

import java.util.function.Function;

/**
 * Creates new events that can be listened to. Based on Fabric's event system.
 *
 * @author Ocelot
 * @see <a href="https://github.com/FabricMC/fabric/blob/1.16/fabric-api-base/src/main/java/net/fabricmc/fabric/api/event/EventFactory.java">EventFactory</a>
 * @since 1.0.0
 */
public final class EventRegistry {

    private EventRegistry() {
    }

    /**
     * Creates a new event for invoking registered listeners.
     *
     * @param type           The type of listener to use
     * @param invokerFactory Factory for Combining multiple listeners into one instance
     * @param <T>            The type of listener
     * @return A new event
     */
    public static <T> PollinatedEvent<T> create(Class<? super T> type, Function<T[], T> invokerFactory) {
        return new PollinatedEventImpl<>(type, invokerFactory);
    }
}
