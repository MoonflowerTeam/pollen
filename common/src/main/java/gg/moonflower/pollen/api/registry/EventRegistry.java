package gg.moonflower.pollen.api.registry;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.core.event.PollinatedEventImpl;
import net.minecraft.world.InteractionResult;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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

    @SuppressWarnings({"unchecked", "SuspiciousInvocationHandlerImplementation"})
    public static <T> PollinatedEvent<T> createLoop(Class<? super T> type) {
        return create(type, events -> (T) Proxy.newProxyInstance(EventRegistry.class.getClassLoader(), new Class[]{type}, (proxy, method, args) -> {
            for (Object event : events) {
                invokeFast(event, method, args);
            }
            return null;
        }));
    }

    @SuppressWarnings({"unchecked", "SuspiciousInvocationHandlerImplementation"})
    public static <T> PollinatedEvent<T> createResult(Class<? super T> type) {
        return create(type, events -> (T) Proxy.newProxyInstance(EventRegistry.class.getClassLoader(), new Class[]{type}, (proxy, method, args) -> {
            for (Object event : events) {
                InteractionResult result = invokeFast(event, method, args);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }
            return InteractionResult.PASS;
        }));
    }

    @SuppressWarnings("unchecked")
    private static <T, S> S invokeFast(T object, Method method, Object[] args) throws Throwable {
        return (S) MethodHandles.lookup().unreflect(method).bindTo(object).invokeWithArguments(args);
    }
}
