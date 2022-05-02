package gg.moonflower.pollen.api.config.fabric;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.InMemoryFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;
import gg.moonflower.pollen.api.platform.Platform;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class PollinatedConfigBuilderImpl implements PollinatedConfigBuilder {

    private final Config storage = Config.of(LinkedHashMap::new, InMemoryFormat.withUniversalSupport()); // Use LinkedHashMap for consistent ordering
    private final Map<List<String>, String> levelComments = new HashMap<>();
    private final List<String> currentPath = new ArrayList<>();
    List<FabricConfigSpec.FabricConfigValue<?>> values = new ArrayList<>();
    private BuilderContext context = new BuilderContext();

    private <T> ConfigValue<T> define(List<String> path, FabricConfigSpec.ValueSpec value, Supplier<T> defaultSupplier) { // This is the root where everything at the end of the day ends up.
        if (!this.currentPath.isEmpty()) {
            List<String> tmp = new ArrayList<>(this.currentPath.size() + path.size());
            tmp.addAll(this.currentPath);
            tmp.addAll(path);
            path = tmp;
        }
        this.storage.set(path, value);
        this.context = new BuilderContext();
        return new FabricConfigSpec.FabricConfigValue<>(this, path, defaultSupplier);
    }

    @Override
    public <T> ConfigValue<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator, Class<?> clazz) {
        this.context.setClazz(clazz);
        return this.define(path, new FabricConfigSpec.ValueSpec(defaultSupplier, validator, this.context), defaultSupplier);
    }

    @Override
    public <V extends Comparable<? super V>> ConfigValue<V> defineInRange(List<String> path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz) {
        FabricConfigSpec.Range<V> range = new FabricConfigSpec.Range<>(clazz, min, max);
        this.context.setRange(range);
        this.context.setComment(ObjectArrays.concat(this.context.getComment(), "Range: " + range));
        if (min.compareTo(max) > 0)
            throw new IllegalArgumentException("Range min most be less then max.");
        return this.define(path, defaultSupplier, range);
    }

    @Override
    public <T> ConfigValue<List<? extends T>> defineList(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
        this.context.setClazz(List.class);
        return this.define(path, new FabricConfigSpec.ValueSpec(defaultSupplier, x -> x instanceof List && ((List<?>) x).stream().allMatch(elementValidator), this.context) {
            @Override
            public Object correct(Object value) {
                if (!(value instanceof List) || ((List<?>) value).isEmpty()) {
                    LogManager.getLogger().debug("List on key {} is deemed to need correction. It is null, not a list, or an empty list. Modders, consider defineListAllowEmpty?", path.get(path.size() - 1));
                    return this.getDefault();
                }
                List<?> list = Lists.newArrayList((List<?>) value);
                list.removeIf(elementValidator.negate());
                if (list.isEmpty()) {
                    LogManager.getLogger().debug("List on key {} is deemed to need correction. It failed validation.", path.get(path.size() - 1));
                    return this.getDefault();
                }
                return list;
            }
        }, defaultSupplier);
    }

    @Override
    public <T> ConfigValue<List<? extends T>> defineListAllowEmpty(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
        this.context.setClazz(List.class);
        return this.define(path, new FabricConfigSpec.ValueSpec(defaultSupplier, x -> x instanceof List && ((List<?>) x).stream().allMatch(elementValidator), this.context) {
            @Override
            public Object correct(Object value) {
                if (!(value instanceof List)) {
                    LogManager.getLogger().debug("List on key {} is deemed to need correction, as it is null or not a list.", path.get(path.size() - 1));
                    return this.getDefault();
                }
                List<?> list = Lists.newArrayList((List<?>) value);
                list.removeIf(elementValidator.negate());
                if (list.isEmpty()) {
                    LogManager.getLogger().debug("List on key {} is deemed to need correction. It failed validation.", path.get(path.size() - 1));
                    return this.getDefault();
                }
                return list;
            }
        }, defaultSupplier);
    }

    @Override
    public <V extends Enum<V>> ConfigValue<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz) {
        this.context.setClazz(clazz);
        V[] allowedValues = clazz.getEnumConstants();
        this.context.setComment(ObjectArrays.concat(this.context.getComment(), "Allowed Values: " + Arrays.stream(allowedValues).filter(validator).map(Enum::name).collect(Collectors.joining(", "))));
        return new FabricConfigSpec.EnumValue<>(this, this.define(path, new FabricConfigSpec.ValueSpec(defaultSupplier, validator, this.context), defaultSupplier).getPath(), defaultSupplier, converter, clazz);
    }

    @Override
    public ConfigValue<Boolean> define(List<String> path, Supplier<Boolean> defaultSupplier) {
        return new FabricConfigSpec.BooleanValue(this, define(path, defaultSupplier, o -> {
            if (o instanceof String)
                return ((String) o).equalsIgnoreCase("true") || ((String) o).equalsIgnoreCase("false");
            return o instanceof Boolean;
        }, Boolean.class).getPath(), defaultSupplier);
    }

    @Override
    public ConfigValue<Double> defineInRange(List<String> path, Supplier<Double> defaultSupplier, double min, double max) {
        return new FabricConfigSpec.DoubleValue(this, this.defineInRange(path, defaultSupplier, min, max, Double.class).getPath(), defaultSupplier);
    }

    @Override
    public ConfigValue<Integer> defineInRange(List<String> path, Supplier<Integer> defaultSupplier, int min, int max) {
        return new FabricConfigSpec.IntValue(this, this.defineInRange(path, defaultSupplier, min, max, Integer.class).getPath(), defaultSupplier);
    }

    @Override
    public ConfigValue<Long> defineInRange(List<String> path, Supplier<Long> defaultSupplier, long min, long max) {
        return new FabricConfigSpec.LongValue(this, this.defineInRange(path, defaultSupplier, min, max, Long.class).getPath(), defaultSupplier);
    }

    @Override
    public PollinatedConfigBuilder comment(String comment) {
        if (comment == null || comment.isEmpty()) {
            comment = "No comment";
            if (!Platform.isProduction())
                LogManager.getLogger().error("Null comment for config option {}, this is invalid and may be disallowed in the future.", FabricConfigSpec.DOT_JOINER.join(this.currentPath));
        }
        this.context.setComment(comment);
        return this;
    }

    @Override
    public PollinatedConfigBuilder comment(String... comment) {
        if (comment == null || comment.length < 1 || (comment.length == 1 && comment[0].isEmpty())) {
            comment = new String[]{"No comment"};
            if (!Platform.isProduction())
                LogManager.getLogger().error("Null comment for config option {}, this is invalid and may be disallowed in the future.", FabricConfigSpec.DOT_JOINER.join(this.currentPath));
        }
        this.context.setComment(comment);
        return this;
    }

    @Override
    public PollinatedConfigBuilder translation(String translationKey) {
        this.context.setTranslationKey(translationKey);
        return this;
    }

    @Override
    public PollinatedConfigBuilder worldRestart() {
        this.context.worldRestart();
        return this;
    }

    @Override
    public PollinatedConfigBuilder push(List<String> path) {
        this.currentPath.addAll(path);
        if (this.context.hasComment()) {
            this.levelComments.put(new ArrayList<>(currentPath), this.context.buildComment());
            this.context.setComment(); // Set to empty
        }
        this.context.ensureEmpty();
        return this;
    }

    @Override
    public PollinatedConfigBuilder pop(int count) {
        if (count > this.currentPath.size())
            throw new IllegalArgumentException("Attempted to pop " + count + " elements when we only had: " + this.currentPath);
        for (int x = 0; x < count; x++)
            this.currentPath.remove(this.currentPath.size() - 1);
        return this;
    }

    public <T> Pair<T, FabricConfigSpec> configure(Function<PollinatedConfigBuilder, T> consumer) {
        T o = consumer.apply(this);
        return Pair.of(o, this.build());
    }

    public FabricConfigSpec build() {
        this.context.ensureEmpty();
        Config valueCfg = Config.of(Config.getDefaultMapCreator(true, true), InMemoryFormat.withSupport(ConfigValue.class::isAssignableFrom));
        this.values.forEach(v -> valueCfg.set(v.getPath(), v));

        FabricConfigSpec ret = new FabricConfigSpec(this.storage, valueCfg, this.levelComments);
        this.values.forEach(v -> v.spec = ret);
        return ret;
    }

    static class BuilderContext {
        private @NotNull String[] comment = new String[0];
        private String langKey;
        private FabricConfigSpec.Range<?> range;
        private boolean worldRestart = false;
        private Class<?> clazz;

        public boolean hasComment() {
            return this.comment.length > 0;
        }

        public String[] getComment() {
            return this.comment;
        }

        public void setComment(String... value) {
            Preconditions.checkNotNull(value, "Comments must not be null");
            this.comment = value;
        }

        public String buildComment() {
            return FabricConfigSpec.LINE_JOINER.join(this.comment);
        }

        public String getTranslationKey() {
            return this.langKey;
        }

        public void setTranslationKey(String value) {
            this.langKey = value;
        }

        @SuppressWarnings("unchecked")
        public <V extends Comparable<? super V>> FabricConfigSpec.Range<V> getRange() {
            return (FabricConfigSpec.Range<V>) this.range;
        }

        public <V extends Comparable<? super V>> void setRange(FabricConfigSpec.Range<V> value) {
            this.range = value;
            this.setClazz(value.getClazz());
        }

        public void worldRestart() {
            this.worldRestart = true;
        }

        public boolean needsWorldRestart() {
            return this.worldRestart;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public void ensureEmpty() {
            validate(this.hasComment(), "Non-empty comment when empty expected");
            validate(this.langKey, "Non-null translation key when null expected");
            validate(this.range, "Non-null range when null expected");
            validate(this.worldRestart, "Dangeling world restart value set to true");
        }

        private void validate(Object value, String message) {
            if (value != null)
                throw new IllegalStateException(message);
        }

        private void validate(boolean value, String message) {
            if (value)
                throw new IllegalStateException(message);
        }
    }
}
