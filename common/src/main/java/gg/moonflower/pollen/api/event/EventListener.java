package gg.moonflower.pollen.api.event;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Denotes a method as listening to an event. This must be placed on all <code>public static</code> methods in a registered event class to receive events.
 *
 * @author Ocelot
 * @see PollinatedEvent
 * @since 1.0.0
 */
@Retention(value = RUNTIME)
@Target(value = METHOD)
public @interface EventListener {

    /**
     * @return The integer priority of this listener receiving events. This only applies to other pollinated events
     */
    int priority() default 1000;

    /**
     * @return Whether events that have already been cancelled should be forwarded to this listener
     */
    boolean receiveCanceled() default false;
}
