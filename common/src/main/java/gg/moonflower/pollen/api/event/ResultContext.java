package gg.moonflower.pollen.api.event;

/**
 * Shared interface for setting event results.
 * <p>If an event is cancellable or has an overriding return type, this is used to manage the result instead.
 *
 * @author ebo2022
 * @since 2.0.0
 */
public interface ResultContext {

    /**
     * @return The current result for the event. This will return {@link EventResult#DEFAULT} if a different result hasn't been set
     */
    EventResult getResult();

    /**
     * Sets a new result for the event.
     *
     * @param result The new result
     */
    void setResult(EventResult result);
}
