package gg.moonflower.pollen.api.util.value;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Allows for storage and modification of the specified data type.
 *
 * @author ebo2022
 * @since 2.0.0
 * @param <T> The type of data to store
 */
public interface ValueHolder<T> extends Supplier<T>, Consumer<T> {

    /**
     * @return A new {@link ValueHolder} initialized with the specified value
     */
    static <T> ValueHolder<T> of(T value) {
        return new DefaultImpl<>(value);
    }

    /**
     * Creates a {@link ValueHolder} that uses an external object to manage its data.
     *
     * @param provider      The class used to retrieve and set the stored value
     * @param valueSupplier A function to retrieve the value from the provider class
     * @param valueSetter   A consumer to externally modify the stored value
     * @return A new {@link ValueHolder} with the specified provider class
     */
    static <T, R> ValueHolder<T> complex(R provider, Function<R, T> valueSupplier, BiConsumer<T, R> valueSetter) {
        return new Complex<>(provider, valueSupplier, valueSetter);
    }

    class DefaultImpl<T> implements ValueHolder<T> {

        private T value;

        private DefaultImpl(T value) {
            this.accept(value);
        }

        @Override
        public void accept(T t) {
            this.value = t;
        }

        @Override
        public T get() {
            return this.value;
        }
    }

    class Complex<T, R> implements ValueHolder<T> {

        private final R provider;
        private final Function<R, T> valueSupplier;
        private final BiConsumer<T, R> valueSetter;

        private Complex(R provider, Function<R, T> valueSupplier, BiConsumer<T, R> valueSetter) {
            this.provider = provider;
            this.valueSupplier = valueSupplier;
            this.valueSetter = valueSetter;
        }

        @Override
        public void accept(T t) {
            this.valueSetter.accept(t, this.provider);
        }

        @Override
        public T get() {
            return this.valueSupplier.apply(this.provider);
        }
    }
}
