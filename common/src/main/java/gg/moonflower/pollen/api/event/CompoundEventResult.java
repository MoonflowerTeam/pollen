package gg.moonflower.pollen.api.event;

import net.minecraft.world.InteractionResultHolder;

/**
 * An {@link EventResult} that can pass on extra data.
 *
 * @author ebo2022
 * @since 2.0.0
 */
public final class CompoundEventResult<T> {

    private final EventResult result;
    private final T object;

    private CompoundEventResult(EventResult result, T object) {
        this.result = result;
        this.object = object;
    }

    /**
     * Prevents further processing and returns a <code>true</code> outcome.
     */
    public static <T> CompoundEventResult<T> allow(T object) {
        return new CompoundEventResult<>(EventResult.ALLOW, object);
    }

    /**
     * Prevents further processing and returns a <code>false</code> outcome.
     */
    public static <T> CompoundEventResult<T> deny(T object) {
        return new CompoundEventResult<>(EventResult.DENY, object);
    }

    /**
     * Keeps processing and continues onto the next iteration.
     * <p>This result never contains extra data.
     */
    public static <T> CompoundEventResult<T> pass() {
        return new CompoundEventResult<>(EventResult.PASS, null);
    }

    /**
     * Halts further processing but does not provide an outcome.
     */
    public static <T> CompoundEventResult<T> stop(T object) {
        return new CompoundEventResult<>(EventResult.STOP, object);
    }

    /**
     * @return Whether this result prevents other listeners from being processed
     */
    public boolean preventsProcessing() {
        return this.result.preventsProcessing();
    }

    /**
     * @return The outcome of this result, returns {@code null} if none is present
     */
    public Boolean getValue() {
        return this.result.getValue();
    }

    /**
     * @return Whether this result has an outcome
     */
    public boolean hasValue() {
        return this.result.hasValue();
    }

    /**
     * @return Whether this result has a {@code true} outcome
     */
    public boolean isTrue() {
        return this.result.isTrue();
    }

    /**
     * @return Whether this result has a {@code false} outcome
     */
    public boolean isFalse() {
        return this.result.isFalse();
    }

    /**
     * @return The {@link EventResult} without extra data attached
     */
    public EventResult getResult() {
        return this.result;
    }

    /**
     * @return The extra data passed on with this result
     */
    public T getObject() {
        return this.object;
    }

    /**
     * @return This result, converted to a vanilla {@link InteractionResultHolder}
     */
    public InteractionResultHolder<T> asInteraction() {
        return new InteractionResultHolder<>(this.result.asInteraction(), this.object);
    }
}
