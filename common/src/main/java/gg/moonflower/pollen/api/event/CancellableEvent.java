package gg.moonflower.pollen.api.event;

/**
 * An event that can be canceled similar to a Forge event.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface CancellableEvent extends PollinatedEvent {

    @Override
    boolean isCancelled();

    /**
     * Marks this event as cancelled. The event can be un-canceled by setting canceled to <code>true</code>.
     *
     * @param cancelled Whether this event should be canceled
     */
    void setCancelled(boolean cancelled);
}
