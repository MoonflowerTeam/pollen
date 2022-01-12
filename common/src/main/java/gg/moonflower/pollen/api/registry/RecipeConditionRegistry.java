package gg.moonflower.pollen.api.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.crafting.condition.PollinatedRecipeCondition;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.resources.ResourceLocation;

/**
 * Registers custom {@link PollinatedRecipeCondition} that are wrapped for each platform.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class RecipeConditionRegistry {

    private RecipeConditionRegistry() {
    }

    /**
     * Registers the specified condition under the specified name.
     *
     * @param name      The id to use for the condition
     * @param condition The actual condition to check with
     */
    @ExpectPlatform
    public static void register(ResourceLocation name, PollinatedRecipeCondition condition) {
        Platform.error();
    }
}
