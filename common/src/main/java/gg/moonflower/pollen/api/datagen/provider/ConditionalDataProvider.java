package gg.moonflower.pollen.api.datagen.provider;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceConditionProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * Provides resource conditions for generated resources.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class ConditionalDataProvider implements DataProvider {

    private final Map<ResourceLocation, List<PollinatedResourceConditionProvider>> providers;

    public ConditionalDataProvider() {
        this.providers = new HashMap<>();
    }

    /**
     * Adds a condition to the specified resource.
     *
     * @param id        The id of the resource to add conditions to
     * @param providers The conditions to add
     */
    protected void addConditions(ResourceLocation id, PollinatedResourceConditionProvider... providers) {
        if (providers.length == 0)
            return;
        this.providers.computeIfAbsent(id, __ -> new ArrayList<>()).addAll(Arrays.asList(providers));
    }

    /**
     * Injects conditions into the specified json.
     *
     * @param id   The id of the resource
     * @param json The generated json for the resource
     */
    protected void injectConditions(ResourceLocation id, JsonObject json) {
        if (this.providers.containsKey(id))
            PollinatedResourceConditionProvider.write(json, this.providers.get(id).toArray(new PollinatedResourceConditionProvider[0]));
    }
}
