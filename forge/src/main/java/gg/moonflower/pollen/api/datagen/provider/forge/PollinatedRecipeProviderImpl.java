package gg.moonflower.pollen.api.datagen.provider.forge;

import net.minecraft.advancements.critereon.*;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@ApiStatus.Internal
public class PollinatedRecipeProviderImpl {

    private static final Map<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> shapeBuilders = ObfuscationReflectionHelper.getPrivateValue(RecipeProvider.class, null, "f_176513_");

    public static void oneToOneConversionRecipe(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2, @Nullable String string) {
        oneToOneConversionRecipe(consumer, itemLike, itemLike2, string, 1);
    }

    public static void oneToOneConversionRecipe(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2, @Nullable String string, int i) {
        ShapelessRecipeBuilder.shapeless(itemLike, i).requires(itemLike2).group(string).unlockedBy(getHasName(itemLike2), has(itemLike2)).save(consumer, getConversionRecipeName(itemLike, itemLike2));
    }

    public static void oreSmelting(Consumer<FinishedRecipe> consumer, List<ItemLike> list, ItemLike itemLike, float f, int i, String string) {
        oreCooking(consumer, RecipeSerializer.SMELTING_RECIPE, list, itemLike, f, i, string, "_from_smelting");
    }

    public static void oreBlasting(Consumer<FinishedRecipe> consumer, List<ItemLike> list, ItemLike itemLike, float f, int i, String string) {
        oreCooking(consumer, RecipeSerializer.BLASTING_RECIPE, list, itemLike, f, i, string, "_from_blasting");
    }

    public static void oreCooking(Consumer<FinishedRecipe> consumer, SimpleCookingSerializer<?> simpleCookingSerializer, List<ItemLike> list, ItemLike itemLike, float f, int i, String string, String string2) {
        for (ItemLike itemLike2 : list) {
            SimpleCookingRecipeBuilder.cooking(Ingredient.of(itemLike2), itemLike, f, i, simpleCookingSerializer).group(string).unlockedBy(getHasName(itemLike2), has(itemLike2)).save(consumer, getItemName(itemLike) + string2 + "_" + getItemName(itemLike2));
        }
    }

    public static void netheriteSmithing(Consumer<FinishedRecipe> consumer, Item item, Item item2) {
        UpgradeRecipeBuilder.smithing(Ingredient.of(item), Ingredient.of(Items.NETHERITE_INGOT), item2).unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT)).save(consumer, getItemName(item2) + "_smithing");
    }

    public static void planksFromLog(Consumer<FinishedRecipe> consumer, ItemLike itemLike, Tag<Item> tag) {
        ShapelessRecipeBuilder.shapeless(itemLike, 4).requires(tag).group("planks").unlockedBy("has_log", has(tag)).save(consumer);
    }

    public static void planksFromLogs(Consumer<FinishedRecipe> consumer, ItemLike itemLike, Tag<Item> tag) {
        ShapelessRecipeBuilder.shapeless(itemLike, 4).requires(tag).group("planks").unlockedBy("has_logs", has(tag)).save(consumer);
    }

    public static void woodFromLogs(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 3).define('#', itemLike2).pattern("##").pattern("##").group("bark").unlockedBy("has_log", has(itemLike2)).save(consumer);
    }

    public static void woodenBoat(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike).define('#', itemLike2).pattern("# #").pattern("###").group("boat").unlockedBy("in_water", insideOf(Blocks.WATER)).save(consumer);
    }

    public static RecipeBuilder buttonBuilder(ItemLike itemLike, Ingredient ingredient) {
        return ShapelessRecipeBuilder.shapeless(itemLike).requires(ingredient);
    }

    public static RecipeBuilder doorBuilder(ItemLike itemLike, Ingredient ingredient) {
        return ShapedRecipeBuilder.shaped(itemLike, 3).define('#', ingredient).pattern("##").pattern("##").pattern("##");
    }

    public static RecipeBuilder fenceBuilder(ItemLike itemLike, Ingredient ingredient) {
        int i = itemLike == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
        Item item = itemLike == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
        return ShapedRecipeBuilder.shaped(itemLike, i).define('W', ingredient).define('#', item).pattern("W#W").pattern("W#W");
    }

    public static RecipeBuilder fenceGateBuilder(ItemLike itemLike, Ingredient ingredient) {
        return ShapedRecipeBuilder.shaped(itemLike).define('#', Items.STICK).define('W', ingredient).pattern("#W#").pattern("#W#");
    }

    public static void pressurePlate(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        pressurePlateBuilder(itemLike, Ingredient.of(itemLike2)).unlockedBy(getHasName(itemLike2), has(itemLike2)).save(consumer);
    }

    public static RecipeBuilder pressurePlateBuilder(ItemLike itemLike, Ingredient ingredient) {
        return ShapedRecipeBuilder.shaped(itemLike).define('#', ingredient).pattern("##");
    }

    public static void slab(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        slabBuilder(itemLike, Ingredient.of(itemLike2)).unlockedBy(getHasName(itemLike2), has(itemLike2)).save(consumer);
    }

    public static RecipeBuilder slabBuilder(ItemLike itemLike, Ingredient ingredient) {
        return ShapedRecipeBuilder.shaped(itemLike, 6).define('#', ingredient).pattern("###");
    }

    public static RecipeBuilder stairBuilder(ItemLike itemLike, Ingredient ingredient) {
        return ShapedRecipeBuilder.shaped(itemLike, 4).define('#', ingredient).pattern("#  ").pattern("## ").pattern("###");
    }

    public static RecipeBuilder trapdoorBuilder(ItemLike itemLike, Ingredient ingredient) {
        return ShapedRecipeBuilder.shaped(itemLike, 2).define('#', ingredient).pattern("###").pattern("###");
    }

    public static RecipeBuilder signBuilder(ItemLike itemLike, Ingredient ingredient) {
        return ShapedRecipeBuilder.shaped(itemLike, 3).group("sign").define('#', ingredient).define('X', Items.STICK).pattern("###").pattern("###").pattern(" X ");
    }

    public static void coloredWoolFromWhiteWoolAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapelessRecipeBuilder.shapeless(itemLike).requires(itemLike2).requires(Blocks.WHITE_WOOL).group("wool").unlockedBy("has_white_wool", has(Blocks.WHITE_WOOL)).save(consumer);
    }

    public static void carpet(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 3).define('#', itemLike2).pattern("##").group("carpet").unlockedBy(getHasName(itemLike2), has(itemLike2)).save(consumer);
    }

    public static void coloredCarpetFromWhiteCarpetAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 8).define('#', Blocks.WHITE_CARPET).define('$', itemLike2).pattern("###").pattern("#$#").pattern("###").group("carpet").unlockedBy("has_white_carpet", has(Blocks.WHITE_CARPET)).unlockedBy(getHasName(itemLike2), has(itemLike2)).save(consumer, getConversionRecipeName(itemLike, Blocks.WHITE_CARPET));
    }

    public static void bedFromPlanksAndWool(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike).define('#', itemLike2).define('X', ItemTags.PLANKS).pattern("###").pattern("XXX").group("bed").unlockedBy(getHasName(itemLike2), has(itemLike2)).save(consumer);
    }

    public static void bedFromWhiteBedAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapelessRecipeBuilder.shapeless(itemLike).requires(Items.WHITE_BED).requires(itemLike2).group("dyed_bed").unlockedBy("has_bed", has(Items.WHITE_BED)).save(consumer, getConversionRecipeName(itemLike, Items.WHITE_BED));
    }

    public static void banner(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike).define('#', itemLike2).define('|', Items.STICK).pattern("###").pattern("###").pattern(" | ").group("banner").unlockedBy(getHasName(itemLike2), has(itemLike2)).save(consumer);
    }

    public static void stainedGlassFromGlassAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 8).define('#', Blocks.GLASS).define('X', itemLike2).pattern("###").pattern("#X#").pattern("###").group("stained_glass").unlockedBy("has_glass", has(Blocks.GLASS)).save(consumer);
    }

    public static void stainedGlassPaneFromStainedGlass(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 16).define('#', itemLike2).pattern("###").pattern("###").group("stained_glass_pane").unlockedBy("has_glass", has(itemLike2)).save(consumer);
    }

    public static void stainedGlassPaneFromGlassPaneAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 8).define('#', Blocks.GLASS_PANE).define('$', itemLike2).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").unlockedBy("has_glass_pane", has(Blocks.GLASS_PANE)).unlockedBy(getHasName(itemLike2), has(itemLike2)).save(consumer, getConversionRecipeName(itemLike, Blocks.GLASS_PANE));
    }

    public static void coloredTerracottaFromTerracottaAndDye(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 8).define('#', Blocks.TERRACOTTA).define('X', itemLike2).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").unlockedBy("has_terracotta", has(Blocks.TERRACOTTA)).save(consumer);
    }

    public static void concretePowder(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapelessRecipeBuilder.shapeless(itemLike, 8).requires(itemLike2).requires(Blocks.SAND, 4).requires(Blocks.GRAVEL, 4).group("concrete_powder").unlockedBy("has_sand", has(Blocks.SAND)).unlockedBy("has_gravel", has(Blocks.GRAVEL)).save(consumer);
    }

    public static void stonecutterResultFromBase(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        stonecutterResultFromBase(consumer, itemLike, itemLike2, 1);
    }

    public static void stonecutterResultFromBase(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2, int i) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(itemLike2), itemLike, i).unlockedBy(getHasName(itemLike2), has(itemLike2)).save(consumer, getConversionRecipeName(itemLike, itemLike2) + "_stonecutting");
    }

    public static void smeltingResultFromBase(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(itemLike2), itemLike, 0.1F, 200).unlockedBy(getHasName(itemLike2), has(itemLike2)).save(consumer);
    }

    public static void nineBlockStorageRecipes(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        nineBlockStorageRecipes(consumer, itemLike, itemLike2, getSimpleRecipeName(itemLike2), null, getSimpleRecipeName(itemLike), null);
    }


    public static void nineBlockStorageRecipesWithCustomPacking(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2, String string, String string2) {
        nineBlockStorageRecipes(consumer, itemLike, itemLike2, string, string2, getSimpleRecipeName(itemLike), null);
    }

    public static void nineBlockStorageRecipesRecipesWithCustomUnpacking(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2, String string, String string2) {
        nineBlockStorageRecipes(consumer, itemLike, itemLike2, getSimpleRecipeName(itemLike2), null, string, string2);
    }

    public static void nineBlockStorageRecipes(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2, String string, @Nullable String string2, String string3, @Nullable String string4) {
        ShapelessRecipeBuilder.shapeless(itemLike, 9).requires(itemLike2).group(string4).unlockedBy(getHasName(itemLike2), has(itemLike2)).save(consumer, new ResourceLocation(string3));
        ShapedRecipeBuilder.shaped(itemLike2).define('#', itemLike).pattern("###").pattern("###").pattern("###").group(string2).unlockedBy(getHasName(itemLike), has(itemLike)).save(consumer, new ResourceLocation(string));
    }

    public static void cookRecipes(Consumer<FinishedRecipe> consumer, String string, SimpleCookingSerializer<?> simpleCookingSerializer, int i) {
        simpleCookingRecipe(consumer, string, simpleCookingSerializer, i, Items.BEEF, Items.COOKED_BEEF, 0.35F);
        simpleCookingRecipe(consumer, string, simpleCookingSerializer, i, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
        simpleCookingRecipe(consumer, string, simpleCookingSerializer, i, Items.COD, Items.COOKED_COD, 0.35F);
        simpleCookingRecipe(consumer, string, simpleCookingSerializer, i, Items.KELP, Items.DRIED_KELP, 0.1F);
        simpleCookingRecipe(consumer, string, simpleCookingSerializer, i, Items.SALMON, Items.COOKED_SALMON, 0.35F);
        simpleCookingRecipe(consumer, string, simpleCookingSerializer, i, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
        simpleCookingRecipe(consumer, string, simpleCookingSerializer, i, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
        simpleCookingRecipe(consumer, string, simpleCookingSerializer, i, Items.POTATO, Items.BAKED_POTATO, 0.35F);
        simpleCookingRecipe(consumer, string, simpleCookingSerializer, i, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
    }

    public static void simpleCookingRecipe(Consumer<FinishedRecipe> consumer, String string, SimpleCookingSerializer<?> simpleCookingSerializer, int i, ItemLike itemLike, ItemLike itemLike2, float f) {
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(itemLike), itemLike2, f, i, simpleCookingSerializer).unlockedBy(getHasName(itemLike), has(itemLike)).save(consumer, getItemName(itemLike2) + "_from_" + string);
    }

    public static void waxRecipes(Consumer<FinishedRecipe> consumer) {
        HoneycombItem.WAXABLES.get().forEach((block, block2) -> ShapelessRecipeBuilder.shapeless(block2).requires(block).requires(Items.HONEYCOMB).group(getItemName(block2)).unlockedBy(getHasName(block), has(block)).save(consumer, getConversionRecipeName(block2, Items.HONEYCOMB)));
    }

    public static void generateRecipes(Consumer<FinishedRecipe> consumer, BlockFamily blockFamily) {
        if (shapeBuilders == null)
            throw new IllegalStateException("Failed to access RecipeProvider#shapeBuilders");
        blockFamily.getVariants().forEach((variant, block) -> {
            BiFunction<ItemLike, ItemLike, RecipeBuilder> biFunction = shapeBuilders.get(variant);
            ItemLike itemLike = getBaseBlock(blockFamily, variant);
            if (biFunction != null) {
                RecipeBuilder recipeBuilder = biFunction.apply(block, itemLike);
                blockFamily.getRecipeGroupPrefix().ifPresent(string -> recipeBuilder.group(string + (variant == BlockFamily.Variant.CUT ? "" : "_" + variant.getName())));
                recipeBuilder.unlockedBy(blockFamily.getRecipeUnlockedBy().orElseGet(() -> getHasName(itemLike)), has(itemLike));
                recipeBuilder.save(consumer);
            }

            if (variant == BlockFamily.Variant.CRACKED) {
                smeltingResultFromBase(consumer, block, itemLike);
            }
        });
    }

    public static Block getBaseBlock(BlockFamily blockFamily, BlockFamily.Variant variant) {
        if (variant == BlockFamily.Variant.CHISELED) {
            if (!blockFamily.getVariants().containsKey(BlockFamily.Variant.SLAB)) {
                throw new IllegalStateException("Slab is not defined for the family.");
            } else {
                return blockFamily.get(BlockFamily.Variant.SLAB);
            }
        } else {
            return blockFamily.getBaseBlock();
        }
    }

    public static EnterBlockTrigger.TriggerInstance insideOf(Block block) {
        return new EnterBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, block, StatePropertiesPredicate.ANY);
    }

    public static InventoryChangeTrigger.TriggerInstance has(MinMaxBounds.Ints ints, ItemLike itemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike).withCount(ints).build());
    }

    public static InventoryChangeTrigger.TriggerInstance has(ItemLike itemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike).build());
    }

    public static InventoryChangeTrigger.TriggerInstance has(Tag<Item> tag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(tag).build());
    }

    public static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... itemPredicates) {
        return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, itemPredicates);
    }

    public static String getHasName(ItemLike itemLike) {
        return "has_" + getItemName(itemLike);
    }

    public static String getItemName(ItemLike itemLike) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(itemLike.asItem())).getPath();
    }

    public static String getSimpleRecipeName(ItemLike itemLike) {
        return getItemName(itemLike);
    }

    public static String getConversionRecipeName(ItemLike itemLike, ItemLike itemLike2) {
        return getItemName(itemLike) + "_from_" + getItemName(itemLike2);
    }

    public static String getSmeltingRecipeName(ItemLike itemLike) {
        return getItemName(itemLike) + "_from_smelting";
    }

    public static String getBlastingRecipeName(ItemLike itemLike) {
        return getItemName(itemLike) + "_from_blasting";
    }
}
