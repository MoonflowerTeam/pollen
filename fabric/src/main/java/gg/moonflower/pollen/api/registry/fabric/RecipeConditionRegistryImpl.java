package gg.moonflower.pollen.api.registry.fabric;

import gg.moonflower.pollen.api.crafting.condition.PollinatedRecipeCondition;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RecipeConditionRegistryImpl {

    public static void register(ResourceLocation name, PollinatedRecipeCondition condition) {
        throw new UnsupportedOperationException("Fabric does not have recipe conditions yet");
    }
}
