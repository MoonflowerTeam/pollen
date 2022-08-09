package gg.moonflower.pollen.api.util.value;

/**
 * Stores a float value and allows for it to be accessed and modified.
 *
 * @author ebo2022
 * @since 2.0.0
 */
public interface FloatValue extends FloatSupplier, FloatConsumer {

    /**
     * A default implementation that can be created with a starting value.
     *
     * @since 2.0.0
     */
    class Simple implements FloatValue {

        private float value;

        public Simple(float value) {
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
}
