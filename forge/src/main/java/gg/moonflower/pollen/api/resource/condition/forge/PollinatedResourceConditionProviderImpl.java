package gg.moonflower.pollen.api.resource.condition.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceConditionProvider;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollinatedResourceConditionProviderImpl {

    public static void write(JsonObject conditionalObject, PollinatedResourceConditionProvider... conditions) {
        if (conditions.length == 0)
            return;

        if (conditionalObject.has("conditions"))
            throw new IllegalArgumentException("Object already has a condition entry: " + conditionalObject);

        JsonArray conditionsJson = new JsonArray();
        for (PollinatedResourceConditionProvider condition : conditions) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", condition.getName().toString());
            condition.write(jsonObject);
            conditionsJson.add(jsonObject);
        }
        conditionalObject.add("conditions", conditionsJson);
    }
}
