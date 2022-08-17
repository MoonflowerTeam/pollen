package gg.moonflower.pollen.api.util.value;

@FunctionalInterface
public interface ObjFloatConsumer<T> {

    void accept(T t, float value);
}
