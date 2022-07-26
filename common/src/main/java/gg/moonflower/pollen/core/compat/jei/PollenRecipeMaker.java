package gg.moonflower.pollen.core.compat.jei;

import gg.moonflower.pollen.api.crafting.PollenRecipeTypes;
import gg.moonflower.pollen.api.crafting.grindstone.PollenGrindstoneRecipe;
import gg.moonflower.pollen.api.crafting.grindstone.PollenShapelessGrindstoneRecipe;
import gg.moonflower.pollen.core.mixin.RecipeManagerAccessor;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ApiStatus.Internal
public final class PollenRecipeMaker {

    private PollenRecipeMaker() {
    }

    public static List<PollenGrindstoneRecipe> getGrindstoneRecipes(IRecipeCategory<PollenGrindstoneRecipe> category, IIngredientManager ingredientManager) {
        List<PollenGrindstoneRecipe> recipes = getRecipes(category, PollenRecipeTypes.GRINDSTONE_TYPE.get());

        Collection<ItemStack> ingredients = ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK);
        Set<Map.Entry<ResourceKey<Enchantment>, Enchantment>> enchantments = Registry.ENCHANTMENT.entrySet();
        for (ItemStack ingredient : ingredients) {
            if (!ingredient.isEnchantable())
                continue;

            Optional<ResourceKey<Item>> idOptional = Registry.ITEM.getResourceKey(ingredient.getItem());
            if (idOptional.isEmpty())
                continue;

            ResourceLocation id = idOptional.get().location();

            Stream<ItemStack> inputs = enchantments.stream().map(Map.Entry::getValue).filter(enchantment -> !enchantment.isCurse()).flatMap(enchantment -> IntStream.rangeClosed(enchantment.getMinLevel(), enchantment.getMaxLevel()).mapToObj(level -> {
                Map<Enchantment, Integer> enchantmentsMap = new HashMap<>(1);
                enchantmentsMap.put(enchantment, level);
                ItemStack input = ingredient.getItem() == Items.BOOK ? new ItemStack(Items.ENCHANTED_BOOK) : ingredient.copy();
                EnchantmentHelper.setEnchantments(enchantmentsMap, input);
                return input;
            }));

            recipes.add(new PollenShapelessGrindstoneRecipe(new ResourceLocation(id.getNamespace(), "disenchant_" + id.getPath()), "", ingredient, NonNullList.of(Ingredient.EMPTY, Ingredient.of(inputs)), -1));
        }
        return recipes;
    }

    public static <C extends Container, T extends Recipe<C>> List<T> getRecipes(@Nullable IRecipeCategory<T> category, RecipeType<T> type) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null)
            return Collections.emptyList();
        return getRecipes(connection.getRecipeManager(), type)
                .stream()
                .filter(recipe -> !recipe.isSpecial())
                .filter(recipe -> category == null || category.isHandled(recipe)).
                collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static <C extends Container, T extends Recipe<C>> Collection<T> getRecipes(RecipeManager recipeManager, RecipeType<T> recipeType) {
        return (Collection<T>) ((RecipeManagerAccessor) recipeManager).invokeByType(recipeType).values();
    }
}
