package gg.moonflower.pollen.api.event;

/**
 * An event that can be canceled similar to a Forge event.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class CancellableEvent implements PollinatedEvent {

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Marks this event as cancelled. The event can be un-canceled by setting canceled to <code>true</code>.
     *
     * @param cancelled Whether this event should be canceled
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
