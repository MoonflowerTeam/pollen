package gg.moonflower.pollen.api.crafting.condition.fabric;

import gg.moonflower.pollen.api.crafting.condition.PollinatedRecipeConditionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
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

    public static PollinatedRecipeConditionProvider blockExists(ResourceLocation name) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedRecipeConditionProvider fluidExists(ResourceLocation name) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedRecipeConditionProvider itemTagPopulated(Tag.Named<Item> tag) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedRecipeConditionProvider blockTagPopulated(Tag.Named<Block> tag) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedRecipeConditionProvider fluidTagPopulated(Tag.Named<Fluid> tag) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedRecipeConditionProvider allModsLoaded(String... modIds) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedRecipeConditionProvider anyModsLoaded(String... modIds) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }
}
