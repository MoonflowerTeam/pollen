package gg.moonflower.pollen.api.util;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.util.ToFloatFunction;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * Stores a boolean value and allows for it to be retrieved and modified.
 *
 * @author ebo2022
 * @since
 */
public interface MutableBoolean extends BooleanSupplier {

    /**
     * @return A new {@link MutableBoolean} initialized with the specified value
     */
    static MutableBoolean of(boolean value) {
        return new DefaultImpl(value);
    }

    /**
     * Creates a {@link MutableBoolean} designed to manipulate Forge event parameters.
     *
     * @param provider      The object used to retrieve and set the stored value
     * @param valueSupplier A function to retrieve the value from the provider
     * @param valueSetter   A consumer to externally modify the stored value
     * @return A new {@link MutableBoolean} with the specified provider class
     */
    static <T> MutableBoolean linkToForge(T provider, Function<T, Boolean> valueSupplier, BiConsumer<T, Boolean> valueSetter) {
        return new ForgeImpl<>(provider, valueSupplier, valueSetter);
    }

    void accept(boolean value);

    class DefaultImpl implements MutableBoolean {

        private boolean value;

        private DefaultImpl(boolean value) {
            this.accept(value);
        }

        @Override
        public void accept(boolean value) {
            this.value = value;
        }

        @Override
        public boolean getAsBoolean() {
            return this.value;
        }
    }

    class ForgeImpl<T> implements MutableBoolean {

        private final T provider;
        private final Function<T, Boolean> valueSupplier;
        private final BiConsumer<T, Boolean> valueSetter;

        private ForgeImpl(T provider, Function<T, Boolean> valueSupplier, BiConsumer<T, Boolean> valueSetter) {
            this.provider = provider;
            this.valueSupplier = valueSupplier;
            this.valueSetter = valueSetter;
        }

        @Override
        public void accept(boolean value) {
            this.valueSetter.accept(this.provider, value);
        }

        @Override
        public boolean getAsBoolean() {
            return this.valueSupplier.apply(this.provider);
        }
    }
}