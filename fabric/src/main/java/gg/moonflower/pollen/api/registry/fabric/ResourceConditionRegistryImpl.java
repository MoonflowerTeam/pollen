package gg.moonflower.pollen.api.registry.fabric;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ResourceConditionRegistryImpl {

    public static void register(ResourceLocation name, PollinatedResourceCondition condition) {
        ResourceConditions.register(name, condition::test);
    }

    public static boolean test(JsonObject json) {
        return ResourceConditions.objectMatchesConditions(json);
    }

    public static String getConditionsKey() {
        return ResourceConditions.CONDITIONS_KEY;
    }
}