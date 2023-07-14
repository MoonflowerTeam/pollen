package gg.moonflower.pollen.core.compat.jei;

import gg.moonflower.pollen.api.crafting.v1.PollenGrindstoneRecipe;
import gg.moonflower.pollen.api.crafting.v1.PollenRecipeTypes;
import gg.moonflower.pollen.core.crafting.PollenShapelessGrindstoneRecipeImpl;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
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
        Minecraft minecraft = Objects.requireNonNull(Minecraft.getInstance(), "minecraft");
        ClientLevel clientLevel = Objects.requireNonNull(minecraft.level, "minecraft world");

        List<PollenGrindstoneRecipe> recipes = getRecipes(clientLevel.getRecipeManager(), category, PollenRecipeTypes.GRINDSTONE_TYPE.get());
        RegistryAccess registryAccess = clientLevel.registryAccess();
        Registry<Item> itemRegistry = registryAccess.registryOrThrow(Registry.ITEM_REGISTRY);
        Registry<Enchantment> enchantmentRegistry = registryAccess.registryOrThrow(Registry.ENCHANTMENT_REGISTRY);

        Collection<ItemStack> ingredients = ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK);
        for (ItemStack ingredient : ingredients) {
            if (!ingredient.isEnchantable()) {
                continue;
            }

            ResourceLocation id = itemRegistry.getKey(ingredient.getItem());
            if (id == null || id == Registry.ITEM.getDefaultKey()) {
                continue;
            }

            Stream<ItemStack> inputs = enchantmentRegistry.stream().filter(enchantment -> !enchantment.isCurse()).flatMap(enchantment -> IntStream.rangeClosed(enchantment.getMinLevel(), enchantment.getMaxLevel()).mapToObj(level -> {
                Map<Enchantment, Integer> enchantmentsMap = new HashMap<>(1);
                enchantmentsMap.put(enchantment, level);
                ItemStack input = ingredient.getItem() == Items.BOOK ? new ItemStack(Items.ENCHANTED_BOOK) : ingredient.copy();
                EnchantmentHelper.setEnchantments(enchantmentsMap, input);
                return input;
            }));

            recipes.add(new PollenShapelessGrindstoneRecipeImpl(new ResourceLocation(id.getNamespace(), "disenchant_" + id.getPath()), "", ingredient, NonNullList.of(Ingredient.EMPTY, Ingredient.of(inputs)), -1));
        }
        return recipes;
    }

    public static <C extends Container, T extends Recipe<C>> List<T> getRecipes(@Nullable IRecipeCategory<T> category, RecipeType<T> type) {
        Minecraft minecraft = Objects.requireNonNull(Minecraft.getInstance(), "minecraft");
        ClientLevel level = Objects.requireNonNull(minecraft.level, "minecraft world");
        return getRecipes(level.getRecipeManager(), category, type);
    }

    public static <C extends Container, T extends Recipe<C>> List<T> getRecipes(RecipeManager recipeManager, @Nullable IRecipeCategory<T> category, RecipeType<T> type) {
        return recipeManager.getAllRecipesFor(type)
                .stream()
                .filter(recipe -> !recipe.isSpecial() && (category == null || category.isHandled(recipe)))
                .collect(Collectors.toList());
    }
}
