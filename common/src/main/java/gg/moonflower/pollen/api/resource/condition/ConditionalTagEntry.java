package gg.moonflower.pollen.api.resource.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.registry.ResourceConditionRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Internal
public class ConditionalTagEntry extends TagEntry {

    private final TagEntry entry;
    private final JsonObject json;

    public ConditionalTagEntry(TagEntry entry, JsonObject json) {
        this.entry = entry;
        this.json = json;
    }

    @Override
    public <T> boolean build(Function<ResourceLocation, Tag<T>> function, Function<ResourceLocation, T> function2, Consumer<T> consumer) {
        return !ResourceConditionRegistry.test(this.json) || this.entry.build(function, function2, consumer);
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

            json.add(ResourceConditionRegistry.getConditionsKey(), this.json.get(ResourceConditionRegistry.getConditionsKey()));
            jsonArray.add(json);
        }
    }

    @Override
    public boolean verifyIfPresent(Predicate<ResourceLocation> predicate, Predicate<ResourceLocation> predicate2) {
        return !ResourceConditionRegistry.test(this.json) || this.entry.verifyIfPresent(predicate, predicate2);
    }
}
