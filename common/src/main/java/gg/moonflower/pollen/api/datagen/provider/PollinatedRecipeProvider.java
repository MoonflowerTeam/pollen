package gg.moonflower.pollen.api.datagen.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Registry;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class PollinatedRecipeProvider extends SimpleConditionalDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;
    private final Map<Block, BlockFamily> blockFamilies;

    public PollinatedRecipeProvider(DataGenerator generator) {
        this.generator = generator;
        this.blockFamilies = new HashMap<>();
    }

    @Override
    public void run(HashCache cache) throws IOException {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = new HashSet<>();
        Consumer<FinishedRecipe> consumer = finishedRecipe -> {
            if (!set.add(finishedRecipe.getId()))
                throw new IllegalStateException("Duplicate recipe " + finishedRecipe.getId());

            try {
                JsonObject json = finishedRecipe.serializeRecipe();
                this.injectConditions(finishedRecipe.getId(), json);
                DataProvider.save(GSON, cache, json, path.resolve("data/" + finishedRecipe.getId().getNamespace() + "/recipes/" + finishedRecipe.getId().getPath() + ".json"));
            } catch (IOException e) {
                LOGGER.error("Couldn't save recipe {}", path, e);
            }

            JsonObject jsonObject = finishedRecipe.serializeAdvancement();
            if (jsonObject != null) {
                try {
                    this.injectConditions(finishedRecipe.getId(), jsonObject);
                    DataProvider.save(GSON, cache, jsonObject, path.resolve("data/" + finishedRecipe.getId().getNamespace() + "/advancements/" + finishedRecipe.getAdvancementId().getPath() + ".json"));
                } catch (IOException e) {
                    LOGGER.error("Couldn't save recipe advancement {}", path, e);
                }
            }
        };
        this.buildRecipes(consumer);
        this.blockFamilies.values().stream().filter(BlockFamily::shouldGenerateRecipe).forEach(family -> generateRecipes(consumer, family)); // Build block family recipes after
    }

    /**
     * Generates all recipes into the specified consumer.
     *
     * @param consumer The registry for recipes
     */
    protected abstract void buildRecipes(Consumer<FinishedRecipe> consumer);

    protected BlockFamily.Builder blockFamily(Block block) {
        BlockFamily.Builder builder = new BlockFamily.Builder(block);
        BlockFamily old = this.blockFamilies.put(block, builder.getFamily());
        if (old != null)
            throw new IllegalStateException("Duplicate family definition for " + Registry.BLOCK.getKey(block));
        return builder;
    }

    public static void oneToOneConversionRecipe(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike ingredient, @Nullable String group) {
        oneToOneConversionRecipe(consumer, result, ingredient, group, 1);
    }

    public static void oneToOneConversionRecipe(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike ingredient, @Nullable String group, int resultAmount) {
        ShapelessRecipeBuilder.shapeless(result, resultAmount)
                .requires(ingredient)
                .group(group)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(consumer, getConversionRecipeName(result, ingredient));
    }

    public static void oreSmelting(Consumer<FinishedRecipe> consumer, List<ItemLike> ingredients, ItemLike result, float experience, int cookingTime, @Nullable String group) {
        oreCooking(consumer, RecipeSerializer.SMELTING_RECIPE, ingredients, result, experience, cookingTime, group, "_from_smelting");
    }

    public static void oreBlasting(Consumer<FinishedRecipe> consumer, List<ItemLike> ingredients, ItemLike result, float experience, int cookingTime, @Nullable String group) {
        oreCooking(consumer, RecipeSerializer.BLASTING_RECIPE, ingredients, result, experience, cookingTime, group, "_from_blasting");
    }

    public static void oreCooking(Consumer<FinishedRecipe> consumer, SimpleCookingSerializer<?> serializer, List<ItemLike> ingredients, ItemLike result, float experience, int cookingTime, @Nullable String group, String from) {
        for (ItemLike itemLike2 : ingredients) {
            ResourceLocation resultName = getItemName(result);
            SimpleCookingRecipeBuilder.cooking(Ingredient.of(itemLike2), result, experience, cookingTime, serializer)
                    .group(group)
                    .unlockedBy(getHasName(itemLike2), has(itemLike2))
                    .save(consumer, new ResourceLocation(resultName.getNamespace(), resultName.getPath() + from + "_" + getItemName(itemLike2).getPath()));
        }

    }

    public static void netheriteSmithing(Consumer<FinishedRecipe> consumer, Item ingredient, Item result) {
        ResourceLocation resultName = getItemName(result);
        UpgradeRecipeBuilder.smithing(Ingredient.of(ingredient), Ingredient.of(Items.NETHERITE_INGOT), result)
                .unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                .save(consumer, new ResourceLocation(resultName.getNamespace(), resultName.getPath() + "_smithing"));
    }

    public static void planksFromLog(Consumer<FinishedRecipe> consumer, ItemLike planks, TagKey<Item> log) {
        ShapelessRecipeBuilder.shapeless(planks, 4).requires(log).group("planks").unlockedBy("has_log", has(log)).save(consumer);
    }

    public static void planksFromLogs(Consumer<FinishedRecipe> consumer, ItemLike planks, TagKey<Item> log) {
        ShapelessRecipeBuilder.shapeless(planks, 4).requires(log).group("planks").unlockedBy("has_logs", has(log)).save(consumer);
    }

    public static void woodFromLogs(Consumer<FinishedRecipe> consumer, ItemLike wood, ItemLike logs) {
        ShapedRecipeBuilder.shaped(wood, 3)
                .define('#', logs)
                .pattern("##")
                .pattern("##")
                .group("bark")
                .unlockedBy("has_log", has(logs))
                .save(consumer);
    }

    public static void woodenBoat(Consumer<FinishedRecipe> consumer, ItemLike boat, ItemLike planks) {
        ShapedRecipeBuilder.shaped(boat)
                .define('#', planks)
                .pattern("# #")
                .pattern("###")
                .group("boat")
                .unlockedBy("in_water", insideOf(Blocks.WATER))
                .save(consumer);
    }

    public static RecipeBuilder buttonBuilder(ItemLike button, Ingredient ingredient) {
        return ShapelessRecipeBuilder.shapeless(button).requires(ingredient);
    }

    public static RecipeBuilder doorBuilder(ItemLike door, Ingredient ingredient) {
        return ShapedRecipeBuilder.shaped(door, 3).define('#', ingredient).pattern("##").pattern("##").pattern("##");
    }

    public static RecipeBuilder fenceBuilder(ItemLike fence, Ingredient planks) {
        int resultAmount = fence == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
        Item stick = fence == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
        return ShapedRecipeBuilder.shaped(fence, resultAmount).define('W', planks).define('#', stick).pattern("W#W").pattern("W#W");
    }

    public static RecipeBuilder fenceGateBuilder(ItemLike fenceGate, Ingredient planks) {
        return ShapedRecipeBuilder.shaped(fenceGate).define('#', Items.STICK).define('W', planks).pattern("#W#").pattern("#W#");
    }

    public static void pressurePlate(Consumer<FinishedRecipe> consumer, ItemLike pressurePlate, ItemLike ingredient) {
        pressurePlateBuilder(pressurePlate, Ingredient.of(ingredient)).unlockedBy(getHasName(ingredient), has(ingredient)).save(consumer);
    }

    public static RecipeBuilder pressurePlateBuilder(ItemLike pressurePlate, Ingredient ingredient) {
        return ShapedRecipeBuilder.shaped(pressurePlate).define('#', ingredient).pattern("##");
    }

    public static void slab(Consumer<FinishedRecipe> consumer, ItemLike slab, ItemLike ingredient) {
        slabBuilder(slab, Ingredient.of(ingredient)).unlockedBy(getHasName(ingredient), has(ingredient)).save(consumer);
    }

    public static RecipeBuilder slabBuilder(ItemLike slab, Ingredient ingredient) {
        return ShapedRecipeBuilder.shaped(slab, 6).define('#', ingredient).pattern("###");
    }

    public static RecipeBuilder stairBuilder(ItemLike stairs, Ingredient ingredient) {
        return ShapedRecipeBuilder.shaped(stairs, 4).define('#', ingredient).pattern("#  ").pattern("## ").pattern("###");
    }

    public static RecipeBuilder trapdoorBuilder(ItemLike trapdoor, Ingredient ingredient) {
        return ShapedRecipeBuilder.shaped(trapdoor, 2).define('#', ingredient).pattern("###").pattern("###");
    }

    public static RecipeBuilder signBuilder(ItemLike sign, Ingredient planks) {
        return ShapedRecipeBuilder.shaped(sign, 3).group("sign").define('#', planks).define('X', Items.STICK).pattern("###").pattern("###").pattern(" X ");
    }

    public static void coloredWoolFromWhiteWoolAndDye(Consumer<FinishedRecipe> consumer, ItemLike wool, ItemLike dye) {
        ShapelessRecipeBuilder.shapeless(wool)
                .requires(dye)
                .requires(Blocks.WHITE_WOOL)
                .group("wool")
                .unlockedBy("has_white_wool", has(Blocks.WHITE_WOOL))
                .save(consumer);
    }

    public static void carpet(Consumer<FinishedRecipe> consumer, ItemLike carpet, ItemLike wool) {
        ShapedRecipeBuilder.shaped(carpet, 3)
                .define('#', wool)
                .pattern("##")
                .group("carpet")
                .unlockedBy(getHasName(wool), has(wool))
                .save(consumer);
    }

    public static void coloredCarpetFromWhiteCarpetAndDye(Consumer<FinishedRecipe> consumer, ItemLike carpet, ItemLike dye) {
        ShapedRecipeBuilder.shaped(carpet, 8)
                .define('#', Blocks.WHITE_CARPET)
                .define('$', dye)
                .pattern("###")
                .pattern("#$#")
                .pattern("###")
                .group("carpet")
                .unlockedBy("has_white_carpet", has(Blocks.WHITE_CARPET))
                .unlockedBy(getHasName(dye), has(dye))
                .save(consumer, getConversionRecipeName(carpet, Blocks.WHITE_CARPET));
    }

    public static void bedFromPlanksAndWool(Consumer<FinishedRecipe> consumer, ItemLike bed, ItemLike wool) {
        ShapedRecipeBuilder.shaped(bed)
                .define('#', wool)
                .define('X', ItemTags.PLANKS)
                .pattern("###")
                .pattern("XXX")
                .group("bed")
                .unlockedBy(getHasName(wool), has(wool))
                .save(consumer);
    }

    public static void bedFromWhiteBedAndDye(Consumer<FinishedRecipe> consumer, ItemLike bed, ItemLike dye) {
        ShapelessRecipeBuilder.shapeless(bed)
                .requires(Items.WHITE_BED)
                .requires(dye)
                .group("dyed_bed")
                .unlockedBy("has_bed", has(Items.WHITE_BED))
                .save(consumer, getConversionRecipeName(bed, Items.WHITE_BED));
    }

    public static void banner(Consumer<FinishedRecipe> consumer, ItemLike banner, ItemLike wool) {
        ShapedRecipeBuilder.shaped(banner)
                .define('#', wool)
                .define('|', Items.STICK)
                .pattern("###")
                .pattern("###")
                .pattern(" | ")
                .group("banner")
                .unlockedBy(getHasName(wool), has(wool))
                .save(consumer);
    }

    public static void stainedGlassFromGlassAndDye(Consumer<FinishedRecipe> consumer, ItemLike stainedGlass, ItemLike dye) {
        ShapedRecipeBuilder.shaped(stainedGlass, 8)
                .define('#', Blocks.GLASS)
                .define('X', dye)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .group("stained_glass")
                .unlockedBy("has_glass", has(Blocks.GLASS))
                .save(consumer);
    }

    public static void stainedGlassPaneFromStainedGlass(Consumer<FinishedRecipe> consumer, ItemLike stainedGlassPane, ItemLike stainedGlass) {
        ShapedRecipeBuilder.shaped(stainedGlassPane, 16)
                .define('#', stainedGlass)
                .pattern("###")
                .pattern("###")
                .group("stained_glass_pane")
                .unlockedBy("has_glass", has(stainedGlass))
                .save(consumer);
    }

    public static void stainedGlassPaneFromGlassPaneAndDye(Consumer<FinishedRecipe> consumer, ItemLike stainedGlassPane, ItemLike dye) {
        ShapedRecipeBuilder.shaped(stainedGlassPane, 8)
                .define('#', Blocks.GLASS_PANE)
                .define('$', dye)
                .pattern("###")
                .pattern("#$#")
                .pattern("###")
                .group("stained_glass_pane")
                .unlockedBy("has_glass_pane", has(Blocks.GLASS_PANE))
                .unlockedBy(getHasName(dye), has(dye))
                .save(consumer, getConversionRecipeName(stainedGlassPane, Blocks.GLASS_PANE));
    }

    public static void coloredTerracottaFromTerracottaAndDye(Consumer<FinishedRecipe> consumer, ItemLike terracotta, ItemLike dye) {
        ShapedRecipeBuilder.shaped(terracotta, 8)
                .define('#', Blocks.TERRACOTTA)
                .define('X', dye)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .group("stained_terracotta")
                .unlockedBy("has_terracotta", has(Blocks.TERRACOTTA))
                .save(consumer);
    }

    public static void concretePowder(Consumer<FinishedRecipe> consumer, ItemLike concretePowder, ItemLike dye) {
        ShapelessRecipeBuilder.shapeless(concretePowder, 8)
                .requires(dye)
                .requires(Blocks.SAND, 4)
                .requires(Blocks.GRAVEL, 4)
                .group("concrete_powder")
                .unlockedBy("has_sand", has(Blocks.SAND))
                .unlockedBy("has_gravel", has(Blocks.GRAVEL))
                .save(consumer);
    }

    public static void candle(Consumer<FinishedRecipe> consumer, ItemLike candle, ItemLike dye) {
        ShapelessRecipeBuilder.shapeless(candle)
                .requires(Blocks.CANDLE)
                .requires(dye)
                .group("dyed_candle")
                .unlockedBy(getHasName(dye), has(dye))
                .save(consumer);
    }

    public static void wall(Consumer<FinishedRecipe> consumer, ItemLike wall, ItemLike ingredient) {
        wallBuilder(wall, Ingredient.of(ingredient)).unlockedBy(getHasName(ingredient), has(ingredient)).save(consumer);
    }

    public static RecipeBuilder wallBuilder(ItemLike wall, Ingredient ingredient) {
        return ShapedRecipeBuilder.shaped(wall, 6).define('#', ingredient).pattern("###").pattern("###");
    }

    public static void polished(Consumer<FinishedRecipe> consumer, ItemLike polished, ItemLike stone) {
        polishedBuilder(polished, Ingredient.of(stone)).unlockedBy(getHasName(stone), has(stone)).save(consumer);
    }

    public static RecipeBuilder polishedBuilder(ItemLike polished, Ingredient stone) {
        return ShapedRecipeBuilder.shaped(polished, 4).define('S', stone).pattern("SS").pattern("SS");
    }

    public static void cut(Consumer<FinishedRecipe> consumer, ItemLike cut, ItemLike stone) {
        cutBuilder(cut, Ingredient.of(stone)).unlockedBy(getHasName(stone), has(stone)).save(consumer);
    }

    public static ShapedRecipeBuilder cutBuilder(ItemLike cut, Ingredient stone) {
        return ShapedRecipeBuilder.shaped(cut, 4).define('#', stone).pattern("##").pattern("##");
    }

    public static void chiseled(Consumer<FinishedRecipe> consumer, ItemLike chiseled, ItemLike stone) {
        chiseledBuilder(chiseled, Ingredient.of(stone)).unlockedBy(getHasName(stone), has(stone)).save(consumer);
    }

    public static ShapedRecipeBuilder chiseledBuilder(ItemLike chisled, Ingredient stone) {
        return ShapedRecipeBuilder.shaped(chisled).define('#', stone).pattern("#").pattern("#");
    }

    public static void stonecutterResultFromBase(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike ingredient) {
        stonecutterResultFromBase(consumer, result, ingredient, 1);
    }

    public static void stonecutterResultFromBase(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike ingredient, int resultAmount) {
        ResourceLocation resultName = getConversionRecipeName(result, ingredient);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), result, resultAmount)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(consumer, new ResourceLocation(resultName.getNamespace(), resultName.getPath() + "_stonecutting"));
    }

    public static void smeltingResultFromBase(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike ingredient) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), result, 0.1F, 200).unlockedBy(getHasName(ingredient), has(ingredient)).save(consumer);
    }

    public static void nineBlockStorageRecipes(Consumer<FinishedRecipe> consumer, ItemLike ingot, ItemLike block) {
        nineBlockStorageRecipes(consumer, ingot, block, getSimpleRecipeName(block), null, getSimpleRecipeName(ingot), null);
    }

    public static void nineBlockStorageRecipesWithCustomPacking(Consumer<FinishedRecipe> consumer, ItemLike ingot, ItemLike block, ResourceLocation blockName, @Nullable String blockGroup) {
        nineBlockStorageRecipes(consumer, ingot, block, blockName, blockGroup, getSimpleRecipeName(ingot), null);
    }

    public static void nineBlockStorageRecipesRecipesWithCustomUnpacking(Consumer<FinishedRecipe> consumer, ItemLike ingot, ItemLike block, ResourceLocation ingotName, @Nullable String ingotGroup) {
        nineBlockStorageRecipes(consumer, ingot, block, getSimpleRecipeName(block), null, ingotName, ingotGroup);
    }

    public static void nineBlockStorageRecipes(Consumer<FinishedRecipe> consumer, ItemLike ingot, ItemLike block, ResourceLocation blockName, @Nullable String blockGroup, ResourceLocation ingotName, @Nullable String ingotGroup) {
        ShapelessRecipeBuilder.shapeless(ingot, 9)
                .requires(block)
                .group(ingotGroup)
                .unlockedBy(getHasName(block), has(block))
                .save(consumer, ingotName);
        ShapedRecipeBuilder.shaped(block)
                .define('#', ingot)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .group(blockGroup)
                .unlockedBy(getHasName(ingot), has(ingot))
                .save(consumer, blockName);
    }

    public static void cookRecipes(Consumer<FinishedRecipe> consumer, String from, SimpleCookingSerializer<?> serializer, int cookingTime) {
        simpleCookingRecipe(consumer, from, serializer, cookingTime, Items.BEEF, Items.COOKED_BEEF, 0.35F);
        simpleCookingRecipe(consumer, from, serializer, cookingTime, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
        simpleCookingRecipe(consumer, from, serializer, cookingTime, Items.COD, Items.COOKED_COD, 0.35F);
        simpleCookingRecipe(consumer, from, serializer, cookingTime, Items.KELP, Items.DRIED_KELP, 0.1F);
        simpleCookingRecipe(consumer, from, serializer, cookingTime, Items.SALMON, Items.COOKED_SALMON, 0.35F);
        simpleCookingRecipe(consumer, from, serializer, cookingTime, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
        simpleCookingRecipe(consumer, from, serializer, cookingTime, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
        simpleCookingRecipe(consumer, from, serializer, cookingTime, Items.POTATO, Items.BAKED_POTATO, 0.35F);
        simpleCookingRecipe(consumer, from, serializer, cookingTime, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
    }

    public static void simpleCookingRecipe(Consumer<FinishedRecipe> consumer, String from, SimpleCookingSerializer<?> serializer, int cookingTime, ItemLike ingredient, ItemLike result, float experience) {
        ResourceLocation resultName = getItemName(result);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(ingredient), result, experience, cookingTime, serializer)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(consumer, new ResourceLocation(resultName.getNamespace(), resultName.getPath() + "_from_" + from));
    }

    public static void generateRecipes(Consumer<FinishedRecipe> consumer, BlockFamily blockFamily) {
        blockFamily.getVariants().forEach((variant, block) -> {
            BiFunction<ItemLike, ItemLike, RecipeBuilder> biFunction = RecipeProvider.shapeBuilders.get(variant);
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

    public static InventoryChangeTrigger.TriggerInstance has(MinMaxBounds.Ints count, ItemLike item) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(item).withCount(count).build());
    }

    public static InventoryChangeTrigger.TriggerInstance has(ItemLike item) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(item).build());
    }

    public static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> itemTag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemTag).build());
    }

    public static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... itemPredicates) {
        return new InventoryChangeTrigger.TriggerInstance(
                EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, itemPredicates
        );
    }

    public static String getHasName(ItemLike item) {
        return "has_" + getItemName(item);
    }

    public static ResourceLocation getItemName(ItemLike item) {
        return Registry.ITEM.getKey(item.asItem());
    }

    public static ResourceLocation getSimpleRecipeName(ItemLike item) {
        return getItemName(item);
    }

    public static ResourceLocation getConversionRecipeName(ItemLike result, ItemLike ingredient) {
        ResourceLocation resultName = getItemName(result);
        return new ResourceLocation(resultName.getNamespace(), resultName.getPath() + "_from_" + getItemName(ingredient).getPath());
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}
