package gg.moonflower.pollen.core.resource.condition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pollen.api.config.ConfigManager;
import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.config.PollinatedModConfig;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceCondition;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceConditionProvider;
import gg.moonflower.pollen.api.util.NumberCompareMode;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;
import java.util.Optional;

@ApiStatus.Internal
public class ConfigResourceCondition implements PollinatedResourceCondition {

    public static final ResourceLocation NAME = new ResourceLocation(Pollen.MOD_ID, "config");

    @Override
    public boolean test(JsonObject json) throws JsonParseException {
        ResourceLocation configId = new ResourceLocation(GsonHelper.getAsString(json, "config"));
        Optional<PollinatedModConfig> optional = ConfigManager.get(configId.getNamespace(), byName(configId.getPath()));
        if (!optional.isPresent() || optional.get().getConfigData() == null)
            return false;

        String configKey = GsonHelper.getAsString(json, "name");
        if (!json.has("value"))
            throw new JsonSyntaxException("Expected 'value'");

        Object entry = optional.get().getConfigData().get(configKey);
        if (entry == null)
            throw new JsonSyntaxException("Unknown config key: " + configKey);

        return testEntry(entry instanceof PollinatedConfigBuilder.ConfigValue<?> ? ((PollinatedConfigBuilder.ConfigValue<?>) entry).get() : entry, json, json.get("value"));
    }

    private static boolean testEntry(Object value, JsonObject json, JsonElement jsonValue) {
        if (value instanceof Number)
            return testNumber((Number) value, json, jsonValue);
        return testSimple(value, jsonValue);
    }

    private static PollinatedConfigType byName(String name) {
        for (PollinatedConfigType type : PollinatedConfigType.values())
            if (type.name().toLowerCase(Locale.ROOT).equals(name))
                return type;
        throw new JsonSyntaxException("Unknown config type: " + name);
    }

    private static boolean testSimple(Object entry, JsonElement value) {
        return String.valueOf(entry).equals(toString(value));
    }

    private static String toString(JsonElement json) {
        if (json == null || json.isJsonNull())
            return "null";
        if (json.isJsonPrimitive())
            return json.getAsString();
        throw new JsonSyntaxException("Unsupported generic config type: " + GsonHelper.getType(json));
    }

    private static boolean testNumber(Number entry, JsonObject json, JsonElement value) {
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber())
            throw new JsonSyntaxException("Expected Number, got " + GsonHelper.getType(value));
        JsonPrimitive primitiveValue = value.getAsJsonPrimitive();
        NumberCompareMode compareMode = json.has("mode") ? NumberCompareMode.byName(GsonHelper.getAsString(json, "mode")) : NumberCompareMode.EQUAL;
        return compareMode.test(entry, primitiveValue.getAsNumber());
    }

    public static class SimpleProvider implements PollinatedResourceConditionProvider {

        private final String modId;
        private final PollinatedConfigType type;
        private final String key;
        private final String value;

        public SimpleProvider(String modId, PollinatedConfigType type, String key, Object value) {
            this.modId = modId;
            this.type = type;
            this.key = key;
            this.value = String.valueOf(value);
        }

        @Override
        public void write(JsonObject json) {
            json.addProperty("config", this.modId + ":" + this.type.name().toLowerCase(Locale.ROOT));
            json.addProperty("name", this.key);
            json.addProperty("value", this.value);
        }

        @Override
        public ResourceLocation getName() {
            return NAME;
        }
    }

    public static class NumberProvider extends SimpleProvider {

        private final NumberCompareMode mode;

        public NumberProvider(String modId, PollinatedConfigType type, String key, Number value, NumberCompareMode mode) {
            super(modId, type, key, value);
            this.mode = mode;
        }

        @Override
        public void write(JsonObject json) {
            super.write(json);
            if (this.mode != NumberCompareMode.EQUAL)
                json.addProperty("mode", this.mode.getSymbol());
        }
    }
}
