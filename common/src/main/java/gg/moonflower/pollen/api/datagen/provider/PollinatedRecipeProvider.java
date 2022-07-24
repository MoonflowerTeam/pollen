package gg.moonflower.pollen.api.datagen.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class PollinatedRecipeProvider extends SimpleConditionalDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;

    public PollinatedRecipeProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(CachedOutput output) throws IOException {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = new HashSet<>();
        this.buildRecipes(finishedRecipe -> {
            if (!set.add(finishedRecipe.getId()))
                throw new IllegalStateException("Duplicate recipe " + finishedRecipe.getId());

            try {
                JsonObject json = finishedRecipe.serializeRecipe();
                this.injectConditions(finishedRecipe.getId(), json);
                DataProvider.saveStable(output, json, path.resolve("data/" + finishedRecipe.getId().getNamespace() + "/recipes/" + finishedRecipe.getId().getPath() + ".json"));
            } catch (IOException e) {
                LOGGER.error("Couldn't save recipe {}", path, e);
            }

            JsonObject jsonObject = finishedRecipe.serializeAdvancement();
            if (jsonObject != null) {
                try {
                    this.injectConditions(finishedRecipe.getId(), jsonObject);
                    DataProvider.saveStable(output, jsonObject, path.resolve("data/" + finishedRecipe.getId().getNamespace() + "/advancements/" + finishedRecipe.getAdvancementId().getPath() + ".json"));
                } catch (IOException e) {
                    LOGGER.error("Couldn't save recipe advancement {}", path, e);
                }
            }
        });
    }

    /**
     * Generates all recipes into the specified consumer.
     *
     * @param consumer The registry for recipes
     */
    protected abstract void buildRecipes(Consumer<FinishedRecipe> consumer);

    public static void netheriteSmithing(Consumer<FinishedRecipe> finishedRecipeConsumer, Item ingredientItem, Item resultItem) {
        UpgradeRecipeBuilder.smithing(Ingredient.of(ingredientItem), Ingredient.of(Items.NETHERITE_INGOT), resultItem).unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT)).save(finishedRecipeConsumer, Registry.ITEM.getKey(resultItem.asItem()).getPath() + "_smithing");
    }

    public static void planksFromLog(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike planks, TagKey<Item> log) {
        ShapelessRecipeBuilder.shapeless(planks, 4).requires(log).group("planks").unlockedBy("has_log", has(log)).save(finishedRecipeConsumer);
    }

    public static void planksFromLogs(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike planks, TagKey<Item> logs) {
        ShapelessRecipeBuilder.shapeless(planks, 4).requires(logs).group("planks").unlockedBy("has_logs", has(logs)).save(finishedRecipeConsumer);
    }

    public static void woodFromLogs(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike wood, ItemLike log) {
        ShapedRecipeBuilder.shaped(wood, 3).define('#', log).pattern("##").pattern("##").group("bark").unlockedBy("has_log", has(log)).save(finishedRecipeConsumer);
    }

    public static void woodenBoat(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike boat, ItemLike material) {
        ShapedRecipeBuilder.shaped(boat).define('#', material).pattern("# #").pattern("###").group("boat").unlockedBy("in_water", insideOf(Blocks.WATER)).save(finishedRecipeConsumer);
    }

    public static void woodenButton(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapelessRecipeBuilder.shapeless(itemLike).requires(itemLike2).group("wooden_button").unlockedBy("has_planks", has(itemLike2)).save(consumer);
    }

    public static void woodenDoor(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 3).define('#', itemLike2).pattern("##").pattern("##").pattern("##").group("wooden_door").unlockedBy("has_planks", has(itemLike2)).save(consumer);
    }

    public static void woodenFence(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 3).define('#', Items.STICK).define('W', itemLike2).pattern("W#W").pattern("W#W").group("wooden_fence").unlockedBy("has_planks", has(itemLike2)).save(consumer);
    }

    public static void woodenFenceGate(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike).define('#', Items.STICK).define('W', itemLike2).pattern("#W#").pattern("#W#").group("wooden_fence_gate").unlockedBy("has_planks", has(itemLike2)).save(consumer);
    }

    public static void woodenPressurePlate(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike).define('#', itemLike2).pattern("##").group("wooden_pressure_plate").unlockedBy("has_planks", has(itemLike2)).save(consumer);
    }

    public static void woodenSlab(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 6).define('#', itemLike2).pattern("###").group("wooden_slab").unlockedBy("has_planks", has(itemLike2)).save(consumer);
    }

    public static void woodenStairs(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 4).define('#', itemLike2).pattern("#  ").pattern("## ").pattern("###").group("wooden_stairs").unlockedBy("has_planks", has(itemLike2)).save(consumer);
    }

    public static void woodenTrapdoor(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        ShapedRecipeBuilder.shaped(itemLike, 2).define('#', itemLike2).pattern("###").pattern("###").group("wooden_trapdoor").unlockedBy("has_planks", has(itemLike2)).save(consumer);
    }

    public static void woodenSign(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        String string = Registry.ITEM.getKey(itemLike2.asItem()).getPath();
        ShapedRecipeBuilder.shaped(itemLike, 3).group("sign").define('#', itemLike2).define('X', Items.STICK).pattern("###").pattern("###").pattern(" X ").unlockedBy("has_" + string, has(itemLike2)).save(consumer);
    }

    public static void coloredWoolFromWhiteWoolAndDye(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike dyedWool, ItemLike dye) {
        ShapelessRecipeBuilder.shapeless(dyedWool).requires(dye).requires(Blocks.WHITE_WOOL).group("wool").unlockedBy("has_white_wool", has(Blocks.WHITE_WOOL)).save(finishedRecipeConsumer);
    }

    public static void carpetFromWool(Consumer<FinishedRecipe> consumer, ItemLike itemLike, ItemLike itemLike2) {
        String string = Registry.ITEM.getKey(itemLike2.asItem()).getPath();
        ShapedRecipeBuilder.shaped(itemLike, 3).define('#', itemLike2).pattern("##").group("carpet").unlockedBy("has_" + string, has(itemLike2)).save(consumer);
    }

    public static void coloredCarpetFromWhiteCarpetAndDye(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike dyedCarpet, ItemLike dye) {
        String string = Registry.ITEM.getKey(dyedCarpet.asItem()).getPath();
        String string2 = Registry.ITEM.getKey(dye.asItem()).getPath();
        ShapedRecipeBuilder.shaped(dyedCarpet, 8).define('#', Blocks.WHITE_CARPET).define('$', dye).pattern("###").pattern("#$#").pattern("###").group("carpet").unlockedBy("has_white_carpet", has(Blocks.WHITE_CARPET)).unlockedBy("has_" + string2, has(dye)).save(finishedRecipeConsumer, string + "_from_white_carpet");
    }

    public static void bedFromPlanksAndWool(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike bed, ItemLike wool) {
        String string = Registry.ITEM.getKey(wool.asItem()).getPath();
        ShapedRecipeBuilder.shaped(bed).define('#', wool).define('X', ItemTags.PLANKS).pattern("###").pattern("XXX").group("bed").unlockedBy("has_" + string, has(wool)).save(finishedRecipeConsumer);
    }

    public static void bedFromWhiteBedAndDye(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike dyedBed, ItemLike dye) {
        String string = Registry.ITEM.getKey(dyedBed.asItem()).getPath();
        ShapelessRecipeBuilder.shapeless(dyedBed).requires(Items.WHITE_BED).requires(dye).group("dyed_bed").unlockedBy("has_bed", has(Items.WHITE_BED)).save(finishedRecipeConsumer, string + "_from_white_bed");
    }

    public static void banner(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike banner, ItemLike material) {
        String string = Registry.ITEM.getKey(material.asItem()).getPath();
        ShapedRecipeBuilder.shaped(banner).define('#', material).define('|', Items.STICK).pattern("###").pattern("###").pattern(" | ").group("banner").unlockedBy("has_" + string, has(material)).save(finishedRecipeConsumer);
    }

    public static void stainedGlassFromGlassAndDye(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike stainedGlass, ItemLike dye) {
        ShapedRecipeBuilder.shaped(stainedGlass, 8).define('#', Blocks.GLASS).define('X', dye).pattern("###").pattern("#X#").pattern("###").group("stained_glass").unlockedBy("has_glass", has(Blocks.GLASS)).save(finishedRecipeConsumer);
    }

    public static void stainedGlassPaneFromStainedGlass(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike stainedGlassPane, ItemLike stainedGlass) {
        ShapedRecipeBuilder.shaped(stainedGlassPane, 16).define('#', stainedGlass).pattern("###").pattern("###").group("stained_glass_pane").unlockedBy("has_glass", has(stainedGlass)).save(finishedRecipeConsumer);
    }

    public static void stainedGlassPaneFromGlassPaneAndDye(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike stainedGlassPane, ItemLike dye) {
        String string = Registry.ITEM.getKey(stainedGlassPane.asItem()).getPath();
        String string2 = Registry.ITEM.getKey(dye.asItem()).getPath();
        ShapedRecipeBuilder.shaped(stainedGlassPane, 8).define('#', Blocks.GLASS_PANE).define('$', dye).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").unlockedBy("has_glass_pane", has(Blocks.GLASS_PANE)).unlockedBy("has_" + string2, has(dye)).save(finishedRecipeConsumer, string + "_from_glass_pane");
    }

    public static void coloredTerracottaFromTerracottaAndDye(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike coloredTerracotta, ItemLike dye) {
        ShapedRecipeBuilder.shaped(coloredTerracotta, 8).define('#', Blocks.TERRACOTTA).define('X', dye).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").unlockedBy("has_terracotta", has(Blocks.TERRACOTTA)).save(finishedRecipeConsumer);
    }

    public static void concretePowder(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike dyedConcretePowder, ItemLike dye) {
        ShapelessRecipeBuilder.shapeless(dyedConcretePowder, 8).requires(dye).requires(Blocks.SAND, 4).requires(Blocks.GRAVEL, 4).group("concrete_powder").unlockedBy("has_sand", has(Blocks.SAND)).unlockedBy("has_gravel", has(Blocks.GRAVEL)).save(finishedRecipeConsumer);
    }

    public static void cookRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer, String cookingMethod, SimpleCookingSerializer<?> cookingSerializer, int cookingTime) {
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.BEEF), Items.COOKED_BEEF, 0.35F, cookingTime, cookingSerializer).unlockedBy("has_beef", has(Items.BEEF)).save(finishedRecipeConsumer, "cooked_beef_from_" + cookingMethod);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35F, cookingTime, cookingSerializer).unlockedBy("has_chicken", has(Items.CHICKEN)).save(finishedRecipeConsumer, "cooked_chicken_from_" + cookingMethod);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.COD), Items.COOKED_COD, 0.35F, cookingTime, cookingSerializer).unlockedBy("has_cod", has(Items.COD)).save(finishedRecipeConsumer, "cooked_cod_from_" + cookingMethod);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Blocks.KELP), Items.DRIED_KELP, 0.1F, cookingTime, cookingSerializer).unlockedBy("has_kelp", has(Blocks.KELP)).save(finishedRecipeConsumer, "dried_kelp_from_" + cookingMethod);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.SALMON), Items.COOKED_SALMON, 0.35F, cookingTime, cookingSerializer).unlockedBy("has_salmon", has(Items.SALMON)).save(finishedRecipeConsumer, "cooked_salmon_from_" + cookingMethod);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.MUTTON), Items.COOKED_MUTTON, 0.35F, cookingTime, cookingSerializer).unlockedBy("has_mutton", has(Items.MUTTON)).save(finishedRecipeConsumer, "cooked_mutton_from_" + cookingMethod);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35F, cookingTime, cookingSerializer).unlockedBy("has_porkchop", has(Items.PORKCHOP)).save(finishedRecipeConsumer, "cooked_porkchop_from_" + cookingMethod);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.POTATO), Items.BAKED_POTATO, 0.35F, cookingTime, cookingSerializer).unlockedBy("has_potato", has(Items.POTATO)).save(finishedRecipeConsumer, "baked_potato_from_" + cookingMethod);
        SimpleCookingRecipeBuilder.cooking(Ingredient.of(Items.RABBIT), Items.COOKED_RABBIT, 0.35F, cookingTime, cookingSerializer).unlockedBy("has_rabbit", has(Items.RABBIT)).save(finishedRecipeConsumer, "cooked_rabbit_from_" + cookingMethod);
    }

    /**
     * Creates a new {@link EnterBlockTrigger} for use with recipe unlock criteria.
     */
    public static EnterBlockTrigger.TriggerInstance insideOf(Block block) {
        return new EnterBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, block, StatePropertiesPredicate.ANY);
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having a certain item.
     */
    public static InventoryChangeTrigger.TriggerInstance has(ItemLike itemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike).build());
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having an item within the given tag.
     */
    public static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> tag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(tag).build());
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having a certain item.
     */
    public static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... predicate) {
        return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, predicate);
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}
