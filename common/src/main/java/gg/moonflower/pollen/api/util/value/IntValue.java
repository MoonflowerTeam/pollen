package gg.moonflower.pollen.api.util.value;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * Stores an integer and allows for it to be accessed and modified.
 *
 * @author ebo2022
 * @since 2.0.0
 */
public interface IntValue extends IntSupplier, IntConsumer {

    /**
     * A default implementation that can be created with a starting value.
     *
     * @since 2.0.0
     */
    class Simple implements IntValue {

        private int value;

        public Simple(int value) {
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
}
