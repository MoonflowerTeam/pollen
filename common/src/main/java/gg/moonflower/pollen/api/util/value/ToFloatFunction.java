package gg.moonflower.pollen.api.util.value;

@FunctionalInterface
public interface ToFloatFunction<T> {

    float applyAsFloat(T value);
}
