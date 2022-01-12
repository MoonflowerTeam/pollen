package gg.moonflower.pollen.api.crafting.condition;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * Supplies JSON objects for accessing conditions for use in data generators.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@ApiStatus.Experimental // TODO test after recipe providers are added
public interface PollinatedRecipeConditionProvider {

    /**
     * Writes the data from this condition into JSON.
     *
     * @param json The JSON to write into
     */
    void write(JsonObject json);

    /**
     * @return The name of the condition this provider is for
     */
    ResourceLocation getName();
}
