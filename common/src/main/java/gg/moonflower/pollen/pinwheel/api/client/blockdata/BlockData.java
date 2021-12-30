package gg.moonflower.pollen.pinwheel.api.client.blockdata;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A container for data accessible in a block.
 *
 * @param <T> The type of data to retrieve
 * @author Ocelot
 * @since 1.0.0
 */
public interface BlockData<T> extends Supplier<T> {

    /**
     * Sets the value of this data.
     *
     * @param value The new value or <code>null</code> to restore defaults
     */
    void set(@Nullable T value);
}
