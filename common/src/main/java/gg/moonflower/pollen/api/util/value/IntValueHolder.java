package gg.moonflower.pollen.api.util.value;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.*;

/**
 * A specialization of {@link ValueHolder} for primitive <code>int</code> values.
 *
 * @author ebo2022
 * @since 2.0.0
 */
public interface IntValueHolder extends IntSupplier, IntConsumer {

    /**
     * @return A new {@link IntValueHolder} initialized with the specified value
     */
    static IntValueHolder of(int value) {
        return new DefaultImpl(value);
    }

    /**
     * Creates a {@link IntValueHolder} that uses an external object to manage its data.
     *
     * @param provider      The class used to retrieve and set the stored value
     * @param valueSupplier A function to retrieve the value from the provider class
     * @param valueSetter   A consumer to externally modify the stored value
     * @return A new {@link IntValueHolder} with the specified provider class
     */
    static <T> IntValueHolder complex(T provider, ToIntFunction<T> valueSupplier, ObjIntConsumer<T> valueSetter) {
        return new Complex<>(provider, valueSupplier, valueSetter);
    }

    class DefaultImpl implements IntValueHolder {

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

    class Complex<T> implements IntValueHolder {

        private final T provider;
        private final ToIntFunction<T> valueSupplier;
        private final ObjIntConsumer<T> valueSetter;

        private Complex(T provider, ToIntFunction<T> valueSupplier, ObjIntConsumer<T> valueSetter) {
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
