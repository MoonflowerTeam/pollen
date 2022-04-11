package gg.moonflower.pollen.api.datagen.provider.fabric;

import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@ApiStatus.Internal
public class PollinatedRecipeProviderImpl {

    public static void oneToOneConversionRecipe(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2, @Nullable String string) {
        RecipeProvider.oneToOneConversionRecipe(consumer, itemLike, itemLike2, string);
    }

    public static void oneToOneConversionRecipe(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2, @Nullable String string, int i) {
        RecipeProvider.oneToOneConversionRecipe(consumer, itemLike, itemLike2, string, i);
    }

    public static void oreSmelting(Consumer<FinishedRecipe> consumer, List<ItemLike> list, ItemLike itemLike, float f, int i, String string) {
        RecipeProvider.oreSmelting(consumer, list, itemLike, f, i, string);
    }

    public static void oreBlasting(Consumer<FinishedRecipe> consumer, List<ItemLike> list, ItemLike itemLike, float f, int i, String string) {
        RecipeProvider.oreBlasting(consumer, list, itemLike, f, i, string);
    }

    public static void oreCooking(Consumer<FinishedRecipe> consumer, SimpleCookingSerializer<?> simpleCookingSerializer, List<ItemLike> list, ItemLike itemLike, float f, int i, String string, String string2) {
        RecipeProvider.oreCooking(consumer, simpleCookingSerializer, list, itemLike, f, i, string, string2);
    }

    public static void netheriteSmithing(Consumer<FinishedRecipe> consumer, Item item, Item item2) {
        RecipeProvider.netheriteSmithing(consumer, item, item2);
    }

    public static void planksFromLog(Consumer<FinishedRecipe> consumer, ItemLike itemLike, Tag<Item> tag) {
        RecipeProvider.planksFromLog(consumer, itemLike, tag);
    }

    public static void planksFromLogs(Consumer<FinishedRecipe> consumer, ItemLike itemLike, Tag<Item> tag) {
        RecipeProvider.planksFromLogs(consumer, itemLike, tag);
    }

    public static void woodFromLogs(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.woodFromLogs(consumer, itemLike, itemLike2);
    }

    public static void woodenBoat(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.woodenBoat(consumer, itemLike, itemLike2);
    }

    public static RecipeBuilder buttonBuilder(ItemLike itemLike, Ingredient ingredient) {
        return RecipeProvider.buttonBuilder(itemLike, ingredient);
    }

    public static RecipeBuilder doorBuilder(ItemLike itemLike, Ingredient ingredient) {
        return RecipeProvider.doorBuilder(itemLike, ingredient);
    }


    public static RecipeBuilder fenceBuilder(ItemLike itemLike, Ingredient ingredient) {
        return RecipeProvider.fenceBuilder(itemLike, ingredient);
    }


    public static RecipeBuilder fenceGateBuilder(ItemLike itemLike, Ingredient ingredient) {
        return RecipeProvider.fenceGateBuilder(itemLike, ingredient);
    }


    public static void pressurePlate(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.pressurePlate(consumer, itemLike, itemLike2);
    }


    public static RecipeBuilder pressurePlateBuilder(ItemLike itemLike, Ingredient ingredient) {
        return RecipeProvider.pressurePlateBuilder(itemLike, ingredient);
    }


    public static void slab(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.slab(consumer, itemLike, itemLike2);
    }


    public static RecipeBuilder slabBuilder(ItemLike itemLike, Ingredient ingredient) {
        return RecipeProvider.slabBuilder(itemLike, ingredient);
    }


    public static RecipeBuilder stairBuilder(ItemLike itemLike, Ingredient ingredient) {
        return RecipeProvider.stairBuilder(itemLike, ingredient);
    }


    public static RecipeBuilder trapdoorBuilder(ItemLike itemLike, Ingredient ingredient) {
        return RecipeProvider.trapdoorBuilder(itemLike, ingredient);
    }

    public static RecipeBuilder signBuilder(ItemLike itemLike, Ingredient ingredient) {
        return RecipeProvider.signBuilder(itemLike, ingredient);
    }

    public static void coloredWoolFromWhiteWoolAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.coloredWoolFromWhiteWoolAndDye(consumer, itemLike, itemLike2);
    }

    public static void carpet(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.carpet(consumer, itemLike, itemLike2);
    }

    public static void coloredCarpetFromWhiteCarpetAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.coloredCarpetFromWhiteCarpetAndDye(consumer, itemLike, itemLike2);
    }

    public static void bedFromPlanksAndWool(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.bedFromPlanksAndWool(consumer, itemLike, itemLike2);
    }

    public static void bedFromWhiteBedAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.bedFromWhiteBedAndDye(consumer, itemLike, itemLike2);
    }

    public static void banner(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.banner(consumer, itemLike, itemLike2);
    }

    public static void stainedGlassFromGlassAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.stainedGlassFromGlassAndDye(consumer, itemLike, itemLike2);
    }

    public static void stainedGlassPaneFromStainedGlass(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.stainedGlassPaneFromStainedGlass(consumer, itemLike, itemLike2);
    }

    public static void stainedGlassPaneFromGlassPaneAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.stainedGlassPaneFromGlassPaneAndDye(consumer, itemLike, itemLike2);
    }

    public static void coloredTerracottaFromTerracottaAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.coloredTerracottaFromTerracottaAndDye(consumer, itemLike, itemLike2);
    }

    public static void concretePowder(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.concretePowder(consumer, itemLike, itemLike2);
    }

    public static void stonecutterResultFromBase(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.stonecutterResultFromBase(consumer, itemLike, itemLike2);
    }

    public static void stonecutterResultFromBase(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2, int i) {
        RecipeProvider.stonecutterResultFromBase(consumer, itemLike, itemLike2, i);
    }

    public static void smeltingResultFromBase(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.smeltingResultFromBase(consumer, itemLike, itemLike2);
    }

    public static void nineBlockStorageRecipes(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        RecipeProvider.nineBlockStorageRecipes(consumer, itemLike, itemLike2);
    }

    public static void nineBlockStorageRecipesWithCustomPacking(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2, String string, String string2) {
        RecipeProvider.nineBlockStorageRecipesWithCustomPacking(consumer, itemLike, itemLike2, string, string2);
    }

    public static void nineBlockStorageRecipesRecipesWithCustomUnpacking(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2, String string, String string2) {
        RecipeProvider.nineBlockStorageRecipesRecipesWithCustomUnpacking(consumer, itemLike, itemLike2, string, string2);
    }

    public static void nineBlockStorageRecipes(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2, String string, @Nullable String string2, String string3, @Nullable String string4) {
        RecipeProvider.nineBlockStorageRecipes(consumer, itemLike, itemLike2, string, string2, string3, string4);
    }

    public static void cookRecipes(Consumer<FinishedRecipe> consumer, String string, SimpleCookingSerializer<?> simpleCookingSerializer, int i) {
        RecipeProvider.cookRecipes(consumer, string, simpleCookingSerializer, i);
    }

    public static void simpleCookingRecipe(Consumer<FinishedRecipe> consumer, String string, SimpleCookingSerializer<?> simpleCookingSerializer, int i, ItemLike itemLike, ItemLike itemLike2, float f) {
        RecipeProvider.simpleCookingRecipe(consumer, string, simpleCookingSerializer, i, itemLike, itemLike2, f);
    }

    public static void waxRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeProvider.waxRecipes(consumer);
    }

    public static void generateRecipes(Consumer<FinishedRecipe> consumer, BlockFamily blockFamily) {
        RecipeProvider.generateRecipes(consumer, blockFamily);
    }

    public static Block getBaseBlock(BlockFamily blockFamily, BlockFamily.Variant variant) {
        return RecipeProvider.getBaseBlock(blockFamily, variant);
    }

    public static EnterBlockTrigger.TriggerInstance insideOf(Block block) {
        return RecipeProvider.insideOf(block);
    }

    public static InventoryChangeTrigger.TriggerInstance has(MinMaxBounds.Ints ints, ItemLike itemLike) {
        return RecipeProvider.has(ints, itemLike);
    }

    public static InventoryChangeTrigger.TriggerInstance has(ItemLike itemLike) {
        return RecipeProvider.has(itemLike);
    }

    public static InventoryChangeTrigger.TriggerInstance has(Tag<Item> tag) {
        return RecipeProvider.has(tag);
    }

    public static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... itemPredicates) {
        return RecipeProvider.inventoryTrigger(itemPredicates);
    }

    public static String getHasName(ItemLike itemLike) {
        return RecipeProvider.getHasName(itemLike);
    }

    public static String getItemName(ItemLike itemLike) {
        return RecipeProvider.getItemName(itemLike);
    }

    public static String getSimpleRecipeName(ItemLike itemLike) {
        return RecipeProvider.getSimpleRecipeName(itemLike);
    }

    public static String getConversionRecipeName(ItemLike itemLike, ItemLike itemLike2) {
        return RecipeProvider.getConversionRecipeName(itemLike, itemLike2);
    }

    public static String getSmeltingRecipeName(ItemLike itemLike) {
        return RecipeProvider.getSmeltingRecipeName(itemLike);
    }

    public static String getBlastingRecipeName(ItemLike itemLike) {
        return RecipeProvider.getBlastingRecipeName(itemLike);
    }
}
