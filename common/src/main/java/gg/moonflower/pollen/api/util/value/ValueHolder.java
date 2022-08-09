package gg.moonflower.pollen.api.util.value;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Allows for storage and modification of the specified data type.
 *
 * @author ebo2022
 * @since 2.0.0
 * @param <T> The type of data
 */
public interface ValueHolder<T> extends Supplier<T>, Consumer<T> {

    /**
     * A default implementation that can be created with a starting value.
     *
     * @since 2.0.0
     */
    class Simple<T> implements ValueHolder<T> {

        private T value;

        public Simple(T value) {
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
}
