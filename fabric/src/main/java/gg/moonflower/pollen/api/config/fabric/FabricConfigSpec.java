package gg.moonflower.pollen.api.config.fabric;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.utils.UnmodifiableConfigWrapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.electronwill.nightconfig.core.ConfigSpec.CorrectionAction.ADD;
import static com.electronwill.nightconfig.core.ConfigSpec.CorrectionAction.REMOVE;
import static com.electronwill.nightconfig.core.ConfigSpec.CorrectionAction.REPLACE;

public class FabricConfigSpec extends UnmodifiableConfigWrapper<UnmodifiableConfig> {

    static final Joiner LINE_JOINER = Joiner.on("\n");
    static final Joiner DOT_JOINER = Joiner.on(".");
    private static final Marker CORE = MarkerManager.getMarker("CORE");
    private final Map<List<String>, String> levelComments;
    private final UnmodifiableConfig values;
    private Config childConfig;

    private boolean isCorrecting = false;

    FabricConfigSpec(UnmodifiableConfig storage, UnmodifiableConfig values, Map<List<String>, String> levelComments) {
        super(storage);
        this.values = values;
        this.levelComments = levelComments;
    }

    public void setConfig(CommentedConfig config) {
        this.childConfig = config;
        if (config != null && !this.isCorrect(config)) {
            String configName = config instanceof FileConfig ? ((FileConfig) config).getNioPath().toString() : config.toString();
            LogManager.getLogger().warn(CORE, "Configuration file {} is not correct. Correcting", configName);
            this.correct(
                    config,
                    (action, path, incorrectValue, correctedValue) -> LogManager.getLogger().warn(CORE, "Incorrect key {} was corrected from {} to its default, {}. {}", DOT_JOINER.join(path), incorrectValue, correctedValue, incorrectValue == correctedValue ? "This seems to be an error." : ""),
                    (action, path, incorrectValue, correctedValue) -> LogManager.getLogger().debug(CORE, "The comment on key {} does not match the spec. This may create a backup.", DOT_JOINER.join(path))
            );
            if (config instanceof FileConfig)
                ((FileConfig) config).save();
        }
        this.afterReload();
    }

    public boolean isCorrecting() {
        return isCorrecting;
    }

    public boolean isLoaded() {
        return this.childConfig != null;
    }

    public UnmodifiableConfig getSpec() {
        return config;
    }

    public UnmodifiableConfig getValues() {
        return values;
    }

    public void afterReload() {
        this.resetCaches(getValues().valueMap().values());
    }

    private void resetCaches(Iterable<Object> configValues) {
        configValues.forEach(value -> {
            if (value instanceof PollinatedConfigBuilder.ConfigValue) {
                PollinatedConfigBuilder.ConfigValue<?> configValue = (PollinatedConfigBuilder.ConfigValue<?>) value;
                configValue.clearCache();
            } else if (value instanceof Config) {
                Config innerConfig = (Config) value;
                this.resetCaches(innerConfig.valueMap().values());
            }
        });
    }

    public void save() {
        Preconditions.checkNotNull(this.childConfig, "Cannot save config value without assigned Config object present");
        if (this.childConfig instanceof FileConfig)
            ((FileConfig) this.childConfig).save();
    }

    public synchronized boolean isCorrect(CommentedConfig config) {
        LinkedList<String> parentPath = new LinkedList<>();
        return correct(this.config, config, parentPath, Collections.unmodifiableList(parentPath), (a, b, c, d) -> {
        }, null, true) == 0;
    }

    public int correct(CommentedConfig config) {
        return correct(config, (action, path, incorrectValue, correctedValue) -> {
        }, null);
    }

    public synchronized int correct(CommentedConfig config, ConfigSpec.CorrectionListener listener) {
        return correct(config, listener, null);
    }

    public synchronized int correct(CommentedConfig config, ConfigSpec.CorrectionListener listener, ConfigSpec.CorrectionListener commentListener) {
        LinkedList<String> parentPath = new LinkedList<>();
        try {
            this.isCorrecting = true;
            return correct(this.config, config, parentPath, Collections.unmodifiableList(parentPath), listener, commentListener, false);
        } finally {
            this.isCorrecting = false;
        }
    }

    // Taken from Forge to keep configs consistent
    private int correct(UnmodifiableConfig spec, CommentedConfig config, LinkedList<String> parentPath, List<String> parentPathUnmodifiable, ConfigSpec.CorrectionListener listener, ConfigSpec.CorrectionListener commentListener, boolean dryRun) {
        int count = 0;

        Map<String, Object> specMap = spec.valueMap();
        Map<String, Object> configMap = config.valueMap();

        for (Map.Entry<String, Object> specEntry : specMap.entrySet()) {
            final String key = specEntry.getKey();
            final Object specValue = specEntry.getValue();
            final Object configValue = configMap.get(key);
            final ConfigSpec.CorrectionAction action = configValue == null ? ADD : REPLACE;

            parentPath.addLast(key);

            if (specValue instanceof Config) {
                if (configValue instanceof CommentedConfig) {
                    count += correct((Config) specValue, (CommentedConfig) configValue, parentPath, parentPathUnmodifiable, listener, commentListener, dryRun);
                    if (count > 0 && dryRun)
                        return count;
                } else if (dryRun) {
                    return 1;
                } else {
                    CommentedConfig newValue = config.createSubConfig();
                    configMap.put(key, newValue);
                    listener.onCorrect(action, parentPathUnmodifiable, configValue, newValue);
                    count++;
                    count += correct((Config) specValue, newValue, parentPath, parentPathUnmodifiable, listener, commentListener, dryRun);
                }

                String newComment = levelComments.get(parentPath);
                String oldComment = config.getComment(key);
                if (stringsNotEqual(oldComment, newComment)) {
                    if (commentListener != null)
                        commentListener.onCorrect(action, parentPathUnmodifiable, oldComment, newComment);

                    if (dryRun)
                        return 1;

                    config.setComment(key, newComment);
                }
            } else {
                ValueSpec valueSpec = (ValueSpec) specValue;
                if (!valueSpec.test(configValue)) {
                    if (dryRun)
                        return 1;

                    Object newValue = valueSpec.correct(configValue);
                    configMap.put(key, newValue);
                    listener.onCorrect(action, parentPathUnmodifiable, configValue, newValue);
                    count++;
                }
                String oldComment = config.getComment(key);
                if (stringsNotEqual(oldComment, valueSpec.getComment())) {
                    if (commentListener != null)
                        commentListener.onCorrect(action, parentPathUnmodifiable, oldComment, valueSpec.getComment());

                    if (dryRun)
                        return 1;

                    config.setComment(key, valueSpec.getComment());
                }
            }

            parentPath.removeLast();
        }

        // Second step: removes the unspecified values
        for (Iterator<Map.Entry<String, Object>> ittr = configMap.entrySet().iterator(); ittr.hasNext(); ) {
            Map.Entry<String, Object> entry = ittr.next();
            if (!specMap.containsKey(entry.getKey())) {
                if (dryRun)
                    return 1;

                ittr.remove();
                parentPath.addLast(entry.getKey());
                listener.onCorrect(REMOVE, parentPathUnmodifiable, entry.getValue(), null);
                parentPath.removeLast();
                count++;
            }
        }
        return count;
    }

    private boolean stringsNotEqual(@Nullable Object obj1, @Nullable Object obj2) {
        if (obj1 instanceof String && obj2 instanceof String) {
            String string1 = (String) obj1;
            String string2 = (String) obj2;

            if (string1.length() > 0 && string2.length() > 0)
                return !string1.replaceAll("\r\n", "\n").equals(string2.replaceAll("\r\n", "\n"));
        }

        // Fallback for when we're not given Strings, or one of them is empty
        return !Objects.equals(obj1, obj2);
    }

    public static class Range<V extends Comparable<? super V>> implements Predicate<Object> {
        private final Class<? extends V> clazz;
        private final V min;
        private final V max;

        Range(Class<V> clazz, V min, V max) {
            this.clazz = clazz;
            this.min = min;
            this.max = max;
        }

        public Class<? extends V> getClazz() {
            return clazz;
        }

        public V getMin() {
            return min;
        }

        public V getMax() {
            return max;
        }

        private boolean isNumber(Object other) {
            return Number.class.isAssignableFrom(clazz) && other instanceof Number;
        }

        @Override
        public boolean test(Object t) {
            if (isNumber(t)) {
                Number n = (Number) t;
                boolean result = ((Number) min).doubleValue() <= n.doubleValue() && n.doubleValue() <= ((Number) max).doubleValue();
                if (!result) {
                    LogManager.getLogger().debug(CORE, "Range value {} is not within its bounds {}-{}", n.doubleValue(), ((Number) min).doubleValue(), ((Number) max).doubleValue());
                }
                return result;
            }
            if (!clazz.isInstance(t)) return false;
            V c = clazz.cast(t);

            boolean result = c.compareTo(min) >= 0 && c.compareTo(max) <= 0;
            if (!result) {
                LogManager.getLogger().debug(CORE, "Range value {} is not within its bounds {}-{}", c, min, max);
            }
            return result;
        }

        public Object correct(Object value, Object def) {
            if (isNumber(value)) {
                Number n = (Number) value;
                return n.doubleValue() < ((Number) min).doubleValue() ? min : n.doubleValue() > ((Number) max).doubleValue() ? max : value;
            }
            if (!clazz.isInstance(value)) return def;
            V c = clazz.cast(value);
            return c.compareTo(min) < 0 ? min : c.compareTo(max) > 0 ? max : value;
        }

        @Override
        public String toString() {
            if (clazz == Integer.class) {
                if (max.equals(Integer.MAX_VALUE)) {
                    return "> " + min;
                } else if (min.equals(Integer.MIN_VALUE)) {
                    return "< " + max;
                }
            } // TODO add more special cases?
            return min + " ~ " + max;
        }
    }

    public static class ValueSpec {
        private final String comment;
        private final String langKey;
        private final Range<?> range;
        private final boolean worldRestart;
        private final Class<?> clazz;
        private final Supplier<?> supplier;
        private final Predicate<Object> validator;
        private Object _default = null;

        ValueSpec(Supplier<?> supplier, Predicate<Object> validator, PollinatedConfigBuilderImpl.BuilderContext context) {
            Objects.requireNonNull(supplier, "Default supplier can not be null");
            Objects.requireNonNull(validator, "Validator can not be null");

            this.comment = context.hasComment() ? context.buildComment() : null;
            this.langKey = context.getTranslationKey();
            this.range = context.getRange();
            this.worldRestart = context.needsWorldRestart();
            this.clazz = context.getClazz();
            this.supplier = supplier;
            this.validator = validator;
        }

        public String getComment() {
            return comment;
        }

        public String getTranslationKey() {
            return langKey;
        }

        @SuppressWarnings("unchecked")
        public <V extends Comparable<? super V>> Range<V> getRange() {
            return (Range<V>) this.range;
        }

        public boolean needsWorldRestart() {
            return this.worldRestart;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public boolean test(Object value) {
            return validator.test(value);
        }

        public Object correct(Object value) {
            return range == null ? getDefault() : range.correct(value, getDefault());
        }

        public Object getDefault() {
            if (_default == null)
                _default = supplier.get();
            return _default;
        }
    }

    public static class FabricConfigValue<T> implements PollinatedConfigBuilder.ConfigValue<T> {
        @VisibleForTesting
        static boolean USE_CACHES = true;

        private final PollinatedConfigBuilderImpl parent;
        private final List<String> path;
        private final Supplier<T> defaultSupplier;
        FabricConfigSpec spec;
        private T cachedValue = null;

        FabricConfigValue(PollinatedConfigBuilderImpl parent, List<String> path, Supplier<T> defaultSupplier) {
            this.parent = parent;
            this.path = path;
            this.defaultSupplier = defaultSupplier;
            this.parent.values.add(this);
        }

        @Override
        public List<String> getPath() {
            return Lists.newArrayList(this.path);
        }

        @Override
        public T get() {
            Preconditions.checkNotNull(this.spec, "Cannot get config value before spec is built");
            if (this.spec.childConfig == null)
                return this.defaultSupplier.get();

            if (USE_CACHES && this.cachedValue == null) {
                this.cachedValue = this.getRaw(this.spec.childConfig, this.path, this.defaultSupplier);
            } else if (!USE_CACHES) {
                return this.getRaw(this.spec.childConfig, this.path, this.defaultSupplier);
            }

            return this.cachedValue;
        }

        protected T getRaw(Config config, List<String> path, Supplier<T> defaultSupplier) {
            return config.getOrElse(path, defaultSupplier);
        }

        @Override
        public PollinatedConfigBuilder next() {
            return parent;
        }

        @Override
        public void save() {
            Preconditions.checkNotNull(this.spec, "Cannot save config value before spec is built");
            Preconditions.checkNotNull(this.spec.childConfig, "Cannot save config value without assigned Config object present");
            this.spec.save();
        }

        @Override
        public void set(T value) {
            Preconditions.checkNotNull(this.spec, "Cannot set config value before spec is built");
            Preconditions.checkNotNull(this.spec.childConfig, "Cannot set config value without assigned Config object present");
            this.spec.childConfig.set(this.path, value);
            this.cachedValue = value;
        }

        @Override
        public void clearCache() {
            this.cachedValue = null;
        }
    }

    public static class BooleanValue extends FabricConfigValue<Boolean> {
        BooleanValue(PollinatedConfigBuilderImpl parent, List<String> path, Supplier<Boolean> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }
    }

    public static class IntValue extends FabricConfigValue<Integer> {
        IntValue(PollinatedConfigBuilderImpl parent, List<String> path, Supplier<Integer> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }

        @Override
        protected Integer getRaw(Config config, List<String> path, Supplier<Integer> defaultSupplier) {
            return config.getIntOrElse(path, defaultSupplier::get);
        }
    }

    public static class LongValue extends FabricConfigValue<Long> {
        LongValue(PollinatedConfigBuilderImpl parent, List<String> path, Supplier<Long> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }

        @Override
        protected Long getRaw(Config config, List<String> path, Supplier<Long> defaultSupplier) {
            return config.getLongOrElse(path, defaultSupplier::get);
        }
    }

    public static class DoubleValue extends FabricConfigValue<Double> {
        DoubleValue(PollinatedConfigBuilderImpl parent, List<String> path, Supplier<Double> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }

        @Override
        protected Double getRaw(Config config, List<String> path, Supplier<Double> defaultSupplier) {
            Number n = config.get(path);
            return n == null ? defaultSupplier.get() : n.doubleValue();
        }
    }

    public static class EnumValue<T extends Enum<T>> extends FabricConfigValue<T> {
        private final EnumGetMethod converter;
        private final Class<T> clazz;

        EnumValue(PollinatedConfigBuilderImpl parent, List<String> path, Supplier<T> defaultSupplier, EnumGetMethod converter, Class<T> clazz) {
            super(parent, path, defaultSupplier);
            this.converter = converter;
            this.clazz = clazz;
        }

        @Override
        protected T getRaw(Config config, List<String> path, Supplier<T> defaultSupplier) {
            return config.getEnumOrElse(path, this.clazz, this.converter, defaultSupplier);
        }
    }
}
