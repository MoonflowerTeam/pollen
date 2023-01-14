package gg.moonflower.pollen.api.util;

import java.util.function.*;

/**
 * Stores an integer value and allows for it to be retrieved and modified.
 *
 * @author ebo2022
 * @since
 */
public interface MutableInt extends IntSupplier, IntConsumer {

    /**
     * @return A new {@link MutableInt} initialized with the specified value
     */
    static MutableInt of(int value) {
        return new DefaultImpl(value);
    }

    /**
     * Creates a {@link MutableInt} designed to manipulate Forge event parameters.
     *
     * @param provider      The object used to retrieve and set the stored value
     * @param valueSupplier A function to retrieve the value from the provider
     * @param valueSetter   A consumer to externally modify the stored value
     * @return A new {@link MutableInt} with the specified provider class
     */
    static <T> MutableInt linkToForge(T provider, ToIntFunction<T> valueSupplier, ObjIntConsumer<T> valueSetter) {
        return new ForgeImpl<>(provider, valueSupplier, valueSetter);
    }

    class DefaultImpl implements MutableInt {

        private int value;

        private DefaultImpl(int value) {
            this.accept(value);
        }

        @Override
        public void accept(int value) {
            this.value = value;
        }

        @Override
        public int getAsInt() {
            return this.value;
        }
    }

    class ForgeImpl<T> implements MutableInt {

        private final T provider;
        private final ToIntFunction<T> valueSupplier;
        private final ObjIntConsumer<T> valueSetter;

        private ForgeImpl(T provider, ToIntFunction<T> valueSupplier, ObjIntConsumer<T> valueSetter) {
            this.provider = provider;
            this.valueSupplier = valueSupplier;
            this.valueSetter = valueSetter;
        }

        @Override
        public void accept(int value) {
            this.valueSetter.accept(this.provider, value);
        }

        @Override
        public int getAsInt() {
            return this.valueSupplier.applyAsInt(this.provider);
        }
    }
}