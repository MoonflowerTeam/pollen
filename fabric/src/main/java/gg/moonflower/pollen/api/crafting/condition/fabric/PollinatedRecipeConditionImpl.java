package gg.moonflower.pollen.api.crafting.condition.fabric;

import gg.moonflower.pollen.api.crafting.condition.PollinatedRecipeConditionProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollinatedRecipeConditionImpl {

    public static PollinatedRecipeConditionProvider and(PollinatedRecipeConditionProvider... values) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedRecipeConditionProvider FALSE() {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedRecipeConditionProvider TRUE() {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedRecipeConditionProvider not(PollinatedRecipeConditionProvider value) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedRecipeConditionProvider or(PollinatedRecipeConditionProvider... values) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedRecipeConditionProvider itemExists(ResourceLocation name) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedRecipeConditionProvider modLoaded(String modId) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }
}
