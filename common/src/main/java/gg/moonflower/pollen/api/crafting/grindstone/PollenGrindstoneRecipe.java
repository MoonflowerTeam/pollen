package gg.moonflower.pollen.api.crafting.grindstone;

import gg.moonflower.pollen.api.crafting.PollenRecipeTypes;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;

/**
 * A recipe intended for a grindstone.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollenGrindstoneRecipe extends Recipe<Container> {

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
