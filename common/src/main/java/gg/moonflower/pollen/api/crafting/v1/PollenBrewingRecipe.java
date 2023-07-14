package gg.moonflower.pollen.api.crafting.v1;

import gg.moonflower.pollen.core.crafting.PollenBrewingRecipeImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * Defines a custom recipe.
 * @author Ocelot
 * @since 2.0.0
 */
public interface PollenBrewingRecipe extends Recipe<Container> {

    static PollenBrewingRecipe create(ResourceLocation id, String group, Potion from, Ingredient ingredient, Potion result) {
        return new PollenBrewingRecipeImpl(id, group, from, ingredient, result);
    }

    Potion getFrom();

    Ingredient getIngredient();

    Potion getResult();

    @Override
    default boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    default ItemStack assemble(Container container) {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean canCraftInDimensions(int w, int h) {
        return w >= 1 && h >= 2;
    }

    @Override
    default ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    default RecipeSerializer<?> getSerializer() {
        return PollenRecipeTypes.BREWING.get();
    }

    @Override
    default RecipeType<?> getType() {
        return PollenRecipeTypes.BREWING_TYPE.get();
    }

    @Override
    default ItemStack getToastSymbol() {
        return new ItemStack(Blocks.BREWING_STAND);
    }
}
