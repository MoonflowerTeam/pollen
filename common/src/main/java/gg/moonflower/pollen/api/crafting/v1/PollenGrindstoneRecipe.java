package gg.moonflower.pollen.api.crafting.v1;

import gg.moonflower.pollen.core.crafting.PollenShapelessGrindstoneRecipeImpl;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;

/**
 * A recipe intended for a grindstone.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface PollenGrindstoneRecipe extends Recipe<Container> {

    /**
     * Creates a new shapeless grindstone recipe.
     *
     * @param id         The id of the recipe
     * @param group      The group in the recipe book
     * @param result     The resulting item
     * @param first      The first ingredient
     * @param second     The second ingredient
     * @param experience The amount of experience rewarded
     * @return A new shapeless grindstone recipe
     */
    static PollenGrindstoneRecipe shapeless(ResourceLocation id, String group, ItemStack result, Ingredient first, Ingredient second, int experience) {
        return new PollenShapelessGrindstoneRecipeImpl(id, group, result, NonNullList.of(Ingredient.EMPTY, first, second), experience);
    }

    /**
     * @return The amount of experience to award the player for this recipe
     */
    int getResultExperience();

    @Override
    default RecipeType<?> getType() {
        return PollenRecipeTypes.GRINDSTONE_TYPE.get();
    }

    @Override
    default ItemStack getToastSymbol() {
        return new ItemStack(Blocks.GRINDSTONE);
    }
}
