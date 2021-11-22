package gg.moonflower.pollen.api.config.fabric;

import com.electronwill.nightconfig.core.EnumGetMethod;
import com.google.common.base.Joiner;
import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedConfigBuilderImpl implements PollinatedConfigBuilder {

    private static final Joiner LINE_JOINER = Joiner.on("\n");
    private static final Joiner DOT_JOINER = Joiner.on(".");

    public static PollinatedConfigBuilder create() {
        return new PollinatedConfigBuilderImpl();
    }

    @Override
    public <T> ConfigValue<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator, Class<?> clazz) {
        return null;
    }

    @Override
    public <V extends Comparable<? super V>> ConfigValue<V> defineInRange(List<String> path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz) {
        return null;
    }

    @Override
    public <T> ConfigValue<List<? extends T>> defineList(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
        return null;
    }

    @Override
    public <T> ConfigValue<List<? extends T>> defineListAllowEmpty(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
        return null;
    }

    @Override
    public <V extends Enum<V>> ConfigValue<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz) {
        return null;
    }

    @Override
    public ConfigValue<Boolean> define(List<String> path, Supplier<Boolean> defaultSupplier) {
        return null;
    }

    @Override
    public ConfigValue<Double> defineInRange(List<String> path, Supplier<Double> defaultSupplier, double min, double max) {
        return null;
    }

    @Override
    public ConfigValue<Integer> defineInRange(List<String> path, Supplier<Integer> defaultSupplier, int min, int max) {
        return null;
    }

    @Override
    public ConfigValue<Long> defineInRange(List<String> path, Supplier<Long> defaultSupplier, long min, long max) {
        return null;
    }

    @Override
    public PollinatedConfigBuilder comment(String comment) {
        return null;
    }

    @Override
    public PollinatedConfigBuilder comment(String... comment) {
        return null;
    }

    @Override
    public PollinatedConfigBuilder translation(String translationKey) {
        return null;
    }

    @Override
    public PollinatedConfigBuilder worldRestart() {
        return null;
    }

    @Override
    public PollinatedConfigBuilder push(List<String> path) {
        return null;
    }

    @Override
    public PollinatedConfigBuilder pop(int count) {
        return null;
    }

    // TODO
    public Object build() {
        return null;
    }
}
