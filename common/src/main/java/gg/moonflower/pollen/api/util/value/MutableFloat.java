package gg.moonflower.pollen.api.util.value;

/**
 * A specialization of {@link MutableValue} for primitive <code>float</code> values.
 *
 * @author ebo2022
 * @since 2.0.0
 */
public interface MutableFloat extends FloatSupplier, FloatConsumer {

    /**
     * @return A new {@link MutableFloat} initialized with the specified value
     */
    static MutableFloat of(float value) {
        return new DefaultImpl(value);
    }

    /**
     * Creates a {@link MutableFloat} that uses an external object to manage its data.
     *
     * @param provider      The class used to retrieve and set the stored value
     * @param valueSupplier A function to retrieve the value from the provider class
     * @param valueSetter   A consumer to externally modify the stored value
     * @return A new {@link MutableFloat} with the specified provider class
     */
    static <T> MutableFloat complex(T provider, ToFloatFunction<T> valueSupplier, ObjFloatConsumer<T> valueSetter) {
        return new Complex<>(provider, valueSupplier, valueSetter);
    }

    class DefaultImpl implements MutableFloat {

        private float value;

        private DefaultImpl(float value) {
            this.accept(value);
        }

        @Override
        public void accept(float value) {
            this.value = value;
        }

        @Override
        public float getAsFloat() {
            return this.value;
        }
    }

    class Complex<T> implements MutableFloat {

        private final T provider;
        private final ToFloatFunction<T> valueSupplier;
        private final ObjFloatConsumer<T> valueSetter;

        private Complex(T provider, ToFloatFunction<T> valueSupplier, ObjFloatConsumer<T> valueSetter) {
            this.provider = provider;
            this.valueSupplier = valueSupplier;
            this.valueSetter = valueSetter;
        }

        @Override
        public void accept(float value) {
            this.valueSetter.accept(this.provider, value);
        }

        @Override
        public float getAsFloat() {
            return this.valueSupplier.applyAsFloat(this.provider);
        }
    }
}
