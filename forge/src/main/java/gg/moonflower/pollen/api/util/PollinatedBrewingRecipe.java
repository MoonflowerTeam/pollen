package gg.moonflower.pollen.api.util;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedBrewingRecipe implements IBrewingRecipe
{
    private final Supplier<Potion> input;
    private final Supplier<Ingredient> ingredient;
    private final Supplier<Potion> result;

    public PollinatedBrewingRecipe(Supplier<Potion> input, Supplier<Ingredient> ingredient, Supplier<Potion> result)
    {
        this.input = input;
        this.ingredient = Suppliers.memoize(ingredient::get);
        this.result = result;
    }

    @Override
    public boolean isInput(ItemStack stack)
    {
        Item item = stack.getItem();
        return (item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION) && PotionUtils.getPotion(stack) == this.input.get();
    }

    @Override
    public boolean isIngredient(ItemStack stack)
    {
        return this.ingredient.get().test(stack);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        if (!input.isEmpty() && !ingredient.isEmpty() && this.isInput(input) && this.isIngredient(ingredient))
            return PotionUtils.setPotion(new ItemStack(input.getItem()), this.result.get());
        return ItemStack.EMPTY;
    }
}
