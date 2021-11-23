package gg.moonflower.pollen.api.config;

import com.electronwill.nightconfig.core.EnumGetMethod;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Implements Forge methods for creating a config.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedConfigBuilder {

    Splitter DOT_SPLITTER = Splitter.on(".");

    @ApiStatus.Internal
    static List<String> split(String path) {
        return Lists.newArrayList(DOT_SPLITTER.split(path));
    }

    //Object
    default <T> ConfigValue<T> define(String path, T defaultValue) {
        return this.define(split(path), defaultValue);
    }

    default <T> ConfigValue<T> define(List<String> path, T defaultValue) {
        return this.define(path, defaultValue, o -> o != null && defaultValue.getClass().isAssignableFrom(o.getClass()));
    }

    default <T> ConfigValue<T> define(String path, T defaultValue, Predicate<Object> validator) {
        return this.define(split(path), defaultValue, validator);
    }

    default <T> ConfigValue<T> define(List<String> path, T defaultValue, Predicate<Object> validator) {
        Objects.requireNonNull(defaultValue, "Default value can not be null");
        return this.define(path, () -> defaultValue, validator);
    }

    default <T> ConfigValue<T> define(String path, Supplier<T> defaultSupplier, Predicate<Object> validator) {
        return this.define(split(path), defaultSupplier, validator);
    }

    default <T> ConfigValue<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator) {
        return this.define(path, defaultSupplier, validator, Object.class);
    }

    <T> ConfigValue<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator, Class<?> clazz);

    default <V extends Comparable<? super V>> ConfigValue<V> defineInRange(String path, V defaultValue, V min, V max, Class<V> clazz) {
        return defineInRange(split(path), defaultValue, min, max, clazz);
    }

    default <V extends Comparable<? super V>> ConfigValue<V> defineInRange(List<String> path, V defaultValue, V min, V max, Class<V> clazz) {
        return defineInRange(path, (Supplier<V>) () -> defaultValue, min, max, clazz);
    }

    default <V extends Comparable<? super V>> ConfigValue<V> defineInRange(String path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz) {
        return defineInRange(split(path), defaultSupplier, min, max, clazz);
    }

    <V extends Comparable<? super V>> ConfigValue<V> defineInRange(List<String> path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz);

    default <T> ConfigValue<T> defineInList(String path, T defaultValue, Collection<? extends T> acceptableValues) {
        return this.defineInList(split(path), defaultValue, acceptableValues);
    }

    default <T> ConfigValue<T> defineInList(String path, Supplier<T> defaultSupplier, Collection<? extends T> acceptableValues) {
        return this.defineInList(split(path), defaultSupplier, acceptableValues);
    }

    default <T> ConfigValue<T> defineInList(List<String> path, T defaultValue, Collection<? extends T> acceptableValues) {
        return this.defineInList(path, () -> defaultValue, acceptableValues);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    default <T> ConfigValue<T> defineInList(List<String> path, Supplier<T> defaultSupplier, Collection<? extends T> acceptableValues) {
        return this.define(path, defaultSupplier, acceptableValues::contains);
    }

    default <T> ConfigValue<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
        return this.defineList(split(path), defaultValue, elementValidator);
    }

    default <T> ConfigValue<List<? extends T>> defineList(String path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
        return this.defineList(split(path), defaultSupplier, elementValidator);
    }

    default <T> ConfigValue<List<? extends T>> defineList(List<String> path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
        return this.defineList(path, () -> defaultValue, elementValidator);
    }

    <T> ConfigValue<List<? extends T>> defineList(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator);

    <T> ConfigValue<List<? extends T>> defineListAllowEmpty(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator);

    //Enum
    default <V extends Enum<V>> ConfigValue<V> defineEnum(String path, V defaultValue) {
        return this.defineEnum(split(path), defaultValue);
    }

    default <V extends Enum<V>> ConfigValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter) {
        return this.defineEnum(split(path), defaultValue, converter);
    }

    default <V extends Enum<V>> ConfigValue<V> defineEnum(List<String> path, V defaultValue) {
        return this.defineEnum(path, defaultValue, defaultValue.getDeclaringClass().getEnumConstants());
    }

    default <V extends Enum<V>> ConfigValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter) {
        return this.defineEnum(path, defaultValue, converter, defaultValue.getDeclaringClass().getEnumConstants());
    }

    @SuppressWarnings("unchecked")
    default <V extends Enum<V>> ConfigValue<V> defineEnum(String path, V defaultValue, V... acceptableValues) {
        return this.defineEnum(split(path), defaultValue, acceptableValues);
    }

    @SuppressWarnings("unchecked")
    default <V extends Enum<V>> ConfigValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, V... acceptableValues) {
        return this.defineEnum(split(path), defaultValue, converter, acceptableValues);
    }

    @SuppressWarnings("unchecked")
    default <V extends Enum<V>> ConfigValue<V> defineEnum(List<String> path, V defaultValue, V... acceptableValues) {
        return this.defineEnum(path, defaultValue, Arrays.asList(acceptableValues));
    }

    @SuppressWarnings("unchecked")
    default <V extends Enum<V>> ConfigValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, V... acceptableValues) {
        return this.defineEnum(path, defaultValue, converter, Arrays.asList(acceptableValues));
    }

    default <V extends Enum<V>> ConfigValue<V> defineEnum(String path, V defaultValue, Collection<V> acceptableValues) {
        return this.defineEnum(split(path), defaultValue, acceptableValues);
    }

    default <V extends Enum<V>> ConfigValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues) {
        return this.defineEnum(split(path), defaultValue, converter, acceptableValues);
    }

    default <V extends Enum<V>> ConfigValue<V> defineEnum(List<String> path, V defaultValue, Collection<V> acceptableValues) {
        return this.defineEnum(path, defaultValue, EnumGetMethod.NAME_IGNORECASE, acceptableValues);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    default <V extends Enum<V>> ConfigValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues) {
        return this.defineEnum(path, defaultValue, converter, obj -> {
            if (obj == null)
                return false;
            if (obj instanceof Enum)
                return acceptableValues.contains(obj);
            try {
                return acceptableValues.contains(converter.get(obj, defaultValue.getDeclaringClass()));
            } catch (IllegalArgumentException | ClassCastException e) {
                return false;
            }
        });
    }

    default <V extends Enum<V>> ConfigValue<V> defineEnum(String path, V defaultValue, Predicate<Object> validator) {
        return this.defineEnum(split(path), defaultValue, validator);
    }

    default <V extends Enum<V>> ConfigValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator) {
        return this.defineEnum(split(path), defaultValue, converter, validator);
    }

    default <V extends Enum<V>> ConfigValue<V> defineEnum(List<String> path, V defaultValue, Predicate<Object> validator) {
        return this.defineEnum(path, () -> defaultValue, validator, defaultValue.getDeclaringClass());
    }

    default <V extends Enum<V>> ConfigValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator) {
        return this.defineEnum(path, () -> defaultValue, converter, validator, defaultValue.getDeclaringClass());
    }

    default <V extends Enum<V>> ConfigValue<V> defineEnum(String path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
        return this.defineEnum(split(path), defaultSupplier, validator, clazz);
    }

    default <V extends Enum<V>> ConfigValue<V> defineEnum(String path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz) {
        return this.defineEnum(split(path), defaultSupplier, converter, validator, clazz);
    }

    default <V extends Enum<V>> ConfigValue<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
        return this.defineEnum(path, defaultSupplier, EnumGetMethod.NAME_IGNORECASE, validator, clazz);
    }

    <V extends Enum<V>> ConfigValue<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz);

    //boolean
    default ConfigValue<Boolean> define(String path, boolean defaultValue) {
        return define(split(path), defaultValue);
    }

    default ConfigValue<Boolean> define(List<String> path, boolean defaultValue) {
        return define(path, () -> defaultValue);
    }

    default ConfigValue<Boolean> define(String path, Supplier<Boolean> defaultSupplier) {
        return define(split(path), defaultSupplier);
    }

    ConfigValue<Boolean> define(List<String> path, Supplier<Boolean> defaultSupplier);

    //Double
    default ConfigValue<Double> defineInRange(String path, double defaultValue, double min, double max) {
        return this.defineInRange(split(path), defaultValue, min, max);
    }

    default ConfigValue<Double> defineInRange(List<String> path, double defaultValue, double min, double max) {
        return this.defineInRange(path, () -> defaultValue, min, max);
    }

    default ConfigValue<Double> defineInRange(String path, Supplier<Double> defaultSupplier, double min, double max) {
        return this.defineInRange(split(path), defaultSupplier, min, max);
    }

    ConfigValue<Double> defineInRange(List<String> path, Supplier<Double> defaultSupplier, double min, double max);

    //Ints
    default ConfigValue<Integer> defineInRange(String path, int defaultValue, int min, int max) {
        return this.defineInRange(split(path), defaultValue, min, max);
    }

    default ConfigValue<Integer> defineInRange(List<String> path, int defaultValue, int min, int max) {
        return this.defineInRange(path, () -> defaultValue, min, max);
    }

    default ConfigValue<Integer> defineInRange(String path, Supplier<Integer> defaultSupplier, int min, int max) {
        return this.defineInRange(split(path), defaultSupplier, min, max);
    }

    ConfigValue<Integer> defineInRange(List<String> path, Supplier<Integer> defaultSupplier, int min, int max);

    //Longs
    default ConfigValue<Long> defineInRange(String path, long defaultValue, long min, long max) {
        return this.defineInRange(split(path), defaultValue, min, max);
    }

    default ConfigValue<Long> defineInRange(List<String> path, long defaultValue, long min, long max) {
        return defineInRange(path, () -> defaultValue, min, max);
    }

    default ConfigValue<Long> defineInRange(String path, Supplier<Long> defaultSupplier, long min, long max) {
        return this.defineInRange(split(path), defaultSupplier, min, max);
    }

    ConfigValue<Long> defineInRange(List<String> path, Supplier<Long> defaultSupplier, long min, long max);

    PollinatedConfigBuilder comment(String comment);

    PollinatedConfigBuilder comment(String... comment);

    PollinatedConfigBuilder translation(String translationKey);

    PollinatedConfigBuilder worldRestart();

    default PollinatedConfigBuilder push(String path) {
        return push(split(path));
    }

    PollinatedConfigBuilder push(List<String> path);

    default PollinatedConfigBuilder pop() {
        return this.pop(1);
    }

    PollinatedConfigBuilder pop(int count);

    interface ConfigValue<T> {
        List<String> getPath();

        T get();

        PollinatedConfigBuilder next();

        void save();

        void set(T value);

        void clearCache();
    }
}
