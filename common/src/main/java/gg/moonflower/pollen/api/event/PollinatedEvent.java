package gg.moonflower.pollen.api.event;

import net.minecraft.world.InteractionResult;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An abstract event that can be fired from {@link EventDispatcher} and listened to.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class PollinatedEvent {

    private boolean cancelled;

    /**
     * An event being canceled means this event will no longer be sent to listeners that do not specify {@link EventListener#receiveCanceled()}.
     *
     * <p>On Forge this will cause Vanilla behavior to take over.
     * <p>On Fabric this will usually cause {@link InteractionResult#FAIL}. The individual event should be checked for how it can be canceled.
     *
     * @return Whether this event is canceled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * @return Whether this event can be cancelled or not. By default, this only checks for the {@link Cancellable} annotation
     */
    public boolean isCancellable() {
        return this.getClass().isAnnotationPresent(Cancellable.class);
    }

    /**
     * Marks this event as cancelled. The event can be un-canceled by setting canceled to <code>false</code>.
     *
     * @param cancelled Whether this event should be canceled
     */
    public void setCancelled(boolean cancelled) {
        if (!this.isCancellable())
            throw new UnsupportedOperationException(this.getClass() + " does not support being cancelled");
        this.cancelled = cancelled;
    }

    /**
     * Marks an event as able to be cancelled.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @Retention(value = RUNTIME)
    @Target(value = TYPE)
    public @interface Cancellable {
    }
}
