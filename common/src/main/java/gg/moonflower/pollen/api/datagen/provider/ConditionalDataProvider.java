package gg.moonflower.pollen.api.datagen.provider;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceConditionProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Provides resource conditions for data providers.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface ConditionalDataProvider extends DataProvider {

    /**
     * Adds a condition to the specified resource.
     *
     * @param id        The id of the resource to add conditions to
     * @param providers The conditions to add
     */
    void addConditions(ResourceLocation id, PollinatedResourceConditionProvider... providers);

    /**
     * Injects conditions into the specified json.
     *
     * @param id   The id of the resource
     * @param json The generated json for the resource
     */
    void injectConditions(ResourceLocation id, JsonObject json);
}
