package gg.moonflower.pollen.api.resource.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.registry.ResourceConditionRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Internal
public class ConditionalTagEntry implements Tag.Entry {

    private final Tag.Entry entry;
    private final JsonElement conditions;

    public ConditionalTagEntry(Tag.Entry entry, JsonElement conditions) {
        this.entry = entry;
        this.conditions = conditions;
    }

    @Override
    public <T> boolean build(Function<ResourceLocation, Tag<T>> function, Function<ResourceLocation, T> function2, Consumer<T> consumer) {
        return true; // Add nothing because this entry failed conditions
    }

    @Override
    public void serializeTo(JsonArray jsonArray) {
        JsonArray entryJson = new JsonArray();
        this.entry.serializeTo(entryJson);

        for (JsonElement element : entryJson) {
            JsonObject json = new JsonObject();
            if (element.isJsonPrimitive()) {
                json.add("id", element);
            } else if (element.isJsonObject()) {
                for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet())
                    json.add(entry.getKey(), entry.getValue());
            }

            json.add(ResourceConditionRegistry.getConditionsKey(), this.conditions);
            jsonArray.add(json);
        }
    }

    @Override
    public boolean verifyIfPresent(Predicate<ResourceLocation> predicate, Predicate<ResourceLocation> predicate2) {
        return true;
    }
}
