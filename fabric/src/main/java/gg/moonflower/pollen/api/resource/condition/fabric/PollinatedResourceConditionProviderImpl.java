package gg.moonflower.pollen.api.resource.condition.fabric;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceConditionProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;

@ApiStatus.Internal
public class PollinatedResourceConditionProviderImpl {

    public static void write(JsonObject conditionalObject, PollinatedResourceConditionProvider... providers) {
        ConditionJsonProvider.write(conditionalObject, providers.length == 0 ? null : Arrays.stream(providers).map(PollinatedResourceConditionImpl::wrap).toArray(ConditionJsonProvider[]::new));
    }
}
