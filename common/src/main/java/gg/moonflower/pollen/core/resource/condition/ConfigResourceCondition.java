package gg.moonflower.pollen.core.resource.condition;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.gson.*;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.config.ConfigManager;
import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.config.PollinatedModConfig;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceCondition;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceConditionProvider;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;

@ApiStatus.Internal
public class ConfigResourceCondition implements PollinatedResourceCondition {

    public static final ResourceLocation NAME = new ResourceLocation(Pollen.MOD_ID, "config");

    private static final ConfigEntry<Object> DEFAULT = new SimpleEntry();
    private static final ConfigEntry<Number> NUMBER = new NumberEntry();

    @Override
    public boolean test(JsonObject json) throws JsonParseException {
        ResourceLocation configId = new ResourceLocation(GsonHelper.getAsString(json, "config"));
        Optional<PollinatedModConfig> optional = ConfigManager.get(configId.getNamespace(), byName(configId.getPath()));
        if (!optional.isPresent())
            return false;

        String configKey = GsonHelper.getAsString(json, "name");
        if (!json.has("value"))
            throw new JsonSyntaxException("Expected 'value'");

        Set<? extends UnmodifiableConfig.Entry> configValues = optional.get().getConfigData().entrySet();
        Optional<? extends UnmodifiableConfig.Entry> entry = configValues.stream().filter(value -> value.getKey().equals(configKey)).findFirst();
        if (!entry.isPresent())
            throw new JsonSyntaxException("Unknown config key: " + configKey);

        Object value = entry.get().getValue();
        return testEntry(value instanceof PollinatedConfigBuilder.ConfigValue<?> ? ((PollinatedConfigBuilder.ConfigValue<?>) value).get() : value, json, json.get("value"));
    }

    private static boolean testEntry(Object value, JsonObject json, JsonElement jsonValue) {
        if (value instanceof Number)
            return NUMBER.test((Number) value, json, jsonValue);
        return DEFAULT.test(value, json, jsonValue);
    }

    private static PollinatedConfigType byName(String name) {
        for (PollinatedConfigType type : PollinatedConfigType.values())
            if (type.name().toLowerCase(Locale.ROOT).equals(name))
                return type;
        throw new JsonSyntaxException("Unknown config type: " + name);
    }

    public static class Provider implements PollinatedResourceConditionProvider {

        @Override
        public void write(JsonObject json) {

        }

        @Override
        public ResourceLocation getName() {
            return NAME;
        }
    }

    @FunctionalInterface
    private interface ConfigEntry<T> {

        boolean test(T entry, JsonObject json, JsonElement value);
    }

    private static class SimpleEntry implements ConfigEntry<Object> {

        @Override
        public boolean test(Object entry, JsonObject json, JsonElement value) {
            return String.valueOf(entry).equals(toString(value));
        }

        private static String toString(JsonElement json) {
            if (json == null || json.isJsonNull())
                return "null";
            if (json.isJsonPrimitive())
                return json.getAsString();
            throw new JsonSyntaxException("Unsupported generic config type: " + GsonHelper.getType(json));
        }
    }

    private static class NumberEntry implements ConfigEntry<Number> {

        @Override
        public boolean test(Number entry, JsonObject json, JsonElement value) {
            if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber())
                throw new JsonSyntaxException("Expected Number, got " + GsonHelper.getType(value));
            JsonPrimitive primitiveValue = value.getAsJsonPrimitive();
            CompareMode compareMode = json.has("mode") ? CompareMode.byName(GsonHelper.getAsString(json, "mode")) : CompareMode.EQUAL;
            return compareMode.comparator.test(entry, primitiveValue.getAsNumber());
        }

        enum CompareMode {
            GREATER_THAN(">", (entry, primitiveValue) -> entry.doubleValue() > primitiveValue.doubleValue()),
            LESS_THAN("<", (entry, primitiveValue) -> entry.doubleValue() < primitiveValue.doubleValue()),
            GREATER_THAN_OR_EQUAL(">=", (entry, primitiveValue) -> entry.doubleValue() >= primitiveValue.doubleValue()),
            LESS_THAN_OR_EQUAL("<=", (entry, primitiveValue) -> entry.doubleValue() <= primitiveValue.doubleValue()),
            EQUAL("=", (entry, primitiveValue) -> entry.doubleValue() == primitiveValue.doubleValue());

            private final String symbol;
            private final BiPredicate<Number, Number> comparator;

            CompareMode(String symbol, BiPredicate<Number, Number> comparator) {
                this.symbol = symbol;
                this.comparator = comparator;
            }

            private static CompareMode byName(String name) {
                for (CompareMode mode : values())
                    if (mode.name().toLowerCase(Locale.ROOT).equals(name) || mode.symbol.equals(name))
                        return mode;
                throw new JsonSyntaxException("Unknown compare mode: " + name);
            }
        }
    }
}
