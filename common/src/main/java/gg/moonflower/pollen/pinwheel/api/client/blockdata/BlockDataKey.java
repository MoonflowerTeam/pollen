package gg.moonflower.pollen.pinwheel.api.client.blockdata;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A key for a specific piece of data at a position.<p>
 * <b><i>NOTE: This data will not be reset until the chunk is removed unless {@link Builder#setFilter(Predicate)} or {@link Builder#setBlocks(Block...)} is called.</i></b>
 *
 * @param <T>
 * @author Ocelot
 * @since 1.0.0
 */
public class BlockDataKey<T> {

    private final Supplier<T> defaultValue;
    private final Predicate<BlockState> filter;

    private BlockDataKey(Supplier<T> defaultValue, Predicate<BlockState> filter) {
        this.defaultValue = defaultValue;
        this.filter = filter;
    }

    /**
     * @return The default value of this key
     */
    public Supplier<T> getDefault() {
        return defaultValue;
    }

    /**
     * @return The filter for whether this key should stay or not
     */
    public Predicate<BlockState> getFilter() {
        return filter;
    }

    /**
     * Creates a new key builder.
     *
     * @param defaultValue The default value to apply if nothing else is provided.
     * @param <T>          The type of data to store
     * @return A builder of that type
     */
    public static <T> Builder<T> of(Supplier<T> defaultValue) {
        return new Builder<>(defaultValue);
    }

    /**
     * Constructs a {@link BlockDataKey} for storing data inside any block.
     *
     * @param <T> The type of data to construct
     * @author Ocelot
     */
    public static class Builder<T> {

        private final Supplier<T> defaultValue;
        private Predicate<BlockState> filter;

        private Builder(Supplier<T> defaultValue) {
            this.defaultValue = defaultValue;
        }

        /**
         * Sets the filter for keeping the key.
         *
         * @param filter The new state filter
         */
        public Builder<T> setFilter(Predicate<BlockState> filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Sets the filter for keeping the key to be for all blocks provided.
         *
         * @param blocks The blocks to allow this key on
         */
        public Builder<T> setBlocks(Block... blocks) {
            this.filter = state ->
            {
                for (Block block : blocks)
                    if (state.is(block))
                        return true;
                return false;
            };
            return this;
        }

        /**
         * @return A new key with the specified parameters
         */
        public BlockDataKey<T> build() {
            return new BlockDataKey<>(this.defaultValue, this.filter != null ? this.filter : state -> true);
        }
    }
}
