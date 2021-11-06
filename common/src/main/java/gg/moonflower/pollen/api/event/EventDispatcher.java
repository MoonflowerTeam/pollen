package gg.moonflower.pollen.api.event;

import net.jodah.typetools.TypeResolver;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Handles the registering and dispatching of common platform events.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class EventDispatcher {

    private static final Map<Class<?>, List<Consumer<? extends PollinatedEvent>>> CLASS_EVENTS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<RegisteredEvent>> EVENT_LISTENERS = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogManager.getLogger();

    @SuppressWarnings("unchecked")
    private static <T extends PollinatedEvent> Class<T> getEventClass(Consumer<T> consumer) {
        Class<T> eventClass = (Class<T>) TypeResolver.resolveRawArgument(Consumer.class, consumer.getClass());
        if ((Class<?>) eventClass == TypeResolver.Unknown.class) {
            LOGGER.error("Failed to resolve handler for \"{}\"", consumer.toString());
            throw new IllegalStateException("Failed to resolve consumer event type: " + consumer);
        }
        return eventClass;
    }

    /**
     * Registers all event handlers in the specified class.
     *
     * @param clazz The class to register
     */
    public static void register(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            EventListener subscribeEvent = method.getDeclaredAnnotation(EventListener.class);
            if (subscribeEvent == null || method.isSynthetic())
                continue;

            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers))
                throw new IllegalStateException("Event handler methods must be public static");
            if (method.getParameterCount() != 1)
                throw new IllegalStateException("All event handlers must have a single event argument");
            if (!PollinatedEvent.class.isAssignableFrom(method.getParameterTypes()[0]))
                throw new IllegalStateException("Event handler arguments are expected to be events");

            Consumer<PollinatedEvent> consumer = event ->
            {
                try {
                    method.invoke(null, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    CrashReport report = CrashReport.forThrowable(e, "Invoking Event Handler");
                    CrashReportCategory category = report.addCategory("Event");
                    category.setDetail("Class", clazz);
                    throw new ReportedException(report);
                }
            };
            CLASS_EVENTS.computeIfAbsent(clazz, key -> new ArrayList<>(methods.length)).add(consumer);
            EVENT_LISTENERS.computeIfAbsent(method.getParameterTypes()[0], key -> new LinkedList<>()).add(new RegisteredEvent(consumer, subscribeEvent.priority(), subscribeEvent.receiveCanceled()));
        }
    }

    /**
     * Registers a single event handler method. The consumer should be kept if you want to later unregister this listener.
     *
     * @param eventConsumer    The consumer of the event
     * @param priority         The priority of the event according to {@link EventListener#priority()}
     * @param receiveCancelled Whether to receive events after being canceled according to {@link EventListener#receiveCanceled()}
     * @param <T>              The type of event to receive
     */
    @SuppressWarnings("unchecked")
    public static <T extends PollinatedEvent> void register(Consumer<T> eventConsumer, int priority, boolean receiveCancelled) {
        Class<?> clazz = getEventClass(eventConsumer);
        EVENT_LISTENERS.computeIfAbsent(clazz, key -> new LinkedList<>()).add(new RegisteredEvent((Consumer<PollinatedEvent>) eventConsumer, priority, receiveCancelled));
    }

    /**
     * Unregisters all event listeners in the specified class.
     *
     * @param clazz The class to unregister
     */
    public static void unregister(Class<?> clazz) {
        List<Consumer<? extends PollinatedEvent>> list = CLASS_EVENTS.remove(clazz);
        if (list == null || list.isEmpty())
            return;
        list.forEach(EventDispatcher::unregister);
    }

    /**
     * Unregisters the specified event listener.
     *
     * @param eventConsumer The consumer registered. It must be the same consumer added before
     * @param <T>           The type of event to unregister
     */
    public static <T extends PollinatedEvent> void unregister(Consumer<T> eventConsumer) {
        Class<?> clazz = getEventClass(eventConsumer);
        List<EventDispatcher.RegisteredEvent> list = EVENT_LISTENERS.get(clazz);
        if (list == null)
            return;
        list.removeIf(registeredEvent -> registeredEvent.consumer == eventConsumer);
        if (list.isEmpty())
            EVENT_LISTENERS.remove(clazz);
    }

    /**
     * Dispatches an event to all listeners.
     *
     * @param event The event to fire
     * @return Whether the event has been canceled
     */
    public static boolean post(PollinatedEvent event) {
        Class<?> clazz = event.getClass();
        AtomicBoolean canceled = new AtomicBoolean(false);
        while (clazz != null && PollinatedEvent.class.isAssignableFrom(clazz)) {
            List<RegisteredEvent> list = EVENT_LISTENERS.get(clazz);
            if (list == null || list.isEmpty())
                continue;

            list.stream().sorted(Comparator.comparingInt(e -> e.priority)).forEachOrdered(registeredEvent ->
            {
                if (canceled.get() && !registeredEvent.receiveCancelled)
                    return;
                registeredEvent.consumer.accept(event);
                if (event.isCancelled())
                    canceled.set(true);
            });

            clazz = clazz.getSuperclass();
        }
        return canceled.get();
    }

    private static class RegisteredEvent {
        private final Consumer<PollinatedEvent> consumer;
        private final int priority;
        private final boolean receiveCancelled;

        private RegisteredEvent(Consumer<PollinatedEvent> consumer, int priority, boolean receiveCancelled) {
            this.consumer = consumer;
            this.priority = priority;
            this.receiveCancelled = receiveCancelled;
        }
    }
}
