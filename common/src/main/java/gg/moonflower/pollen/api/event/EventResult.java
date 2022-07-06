package gg.moonflower.pollen.api.event;

import net.minecraft.world.InteractionResult;
import org.apache.commons.lang3.BooleanUtils;

/**
 * A wrapper for event results.
 *
 * @see CompoundEventResult
 * @author ebo2022
 * @since 2.0.0
 */
public enum EventResult {

    /**
     * Prevents further processing and returns a <code>true</code> outcome.
     */
    ALLOW(true, true),

    /**
     * Prevents further processing and returns a <code>false</code> outcome.
     */
    DENY(true, false),

    /**
     * Keeps processing and continues onto the next iteration.
     */
    PASS(false, null),

    /**
     * Halts further processing but does not provide an outcome.
     */
    STOP(true, null);

    private final boolean preventsProcessing;
    private final Boolean value;

    EventResult(boolean preventsProcessing, Boolean value) {
        this.preventsProcessing = preventsProcessing;
        this.value = value;
    }

    /**
     * @return Whether the result prevents other listeners from being processed
     */
    public boolean preventsProcessing() {
        return preventsProcessing;
    }

    /**
     * @return The boolean outcome of the result, returns <code>null</code> if there is none
     */
    public Boolean getValue() {
        return value;
    }

    /**
     * @return Whether the result has an outcome
     */
    public boolean hasValue() {
        return value != null;
    }

    /**
     * @return Whether the result has a <code>true</code> outcome
     */
    public boolean isTrue() {
        return BooleanUtils.isTrue(value);
    }

    /**
     * @return Whether the result has a <code>false</code> outcome
     */
    public boolean isFalse() {
        return BooleanUtils.isFalse(value);
    }

    /**
     * @return The result as a vanilla-sided {@link InteractionResult}
     */
    public InteractionResult asInteraction() {
        if (hasValue()) return getValue() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        return InteractionResult.PASS;
    }
}


