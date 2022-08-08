package gg.moonflower.pollen.api.event;

/**
 * Wrapper for Forge event results.
 *
 * @author ebo2022
 * @since 2.0.0
 */
public enum EventResult {

    /**
     * Represents a false outcome of an event. Usage varies depending on the event.
     */
    DENY,

    /**
     * Represents the default outcome of an event, which usually means continuing onto other listeners and/or using vanilla logic.
     */
    PASS,

    /**
     * Represents a successful outcome of an event.
     */
    ALLOW
}
