package gg.moonflower.pollen.api.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.crafting.PollenRecipeTypes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class GrindstoneRecipeBuilder {

    private final Item result;
    private final int count;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private String group;
    private int experience;

    public GrindstoneRecipeBuilder(ItemLike result, int count) {
        this.result = result.asItem();
        this.count = count;
    }

    /**
     * Creates a new builder for a grindstone recipe.
     */
    public static GrindstoneRecipeBuilder grindstone(ItemLike result) {
        return new GrindstoneRecipeBuilder(result, 1);
    }

    /**
     * Creates a new builder for a grindstone recipe.
     */
    public static GrindstoneRecipeBuilder grindstone(ItemLike result, int count) {
        return new GrindstoneRecipeBuilder(result, count);
    }

    /**
     * Adds an ingredient that can be any item in the given tag.
     */
    public GrindstoneRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(tag));
    }

    /**
     * Adds an ingredient of the given item.
     */
    public GrindstoneRecipeBuilder requires(ItemLike item) {
        return this.requires(item, 1);
    }

    /**
     * Adds the given ingredient multiple times.
     */
    public GrindstoneRecipeBuilder requires(ItemLike item, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.requires(Ingredient.of(item));
        }

        return this;
    }

    /**
     * Adds an ingredient.
     */
    public GrindstoneRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    /**
     * Adds an ingredient multiple times.
     */
    public GrindstoneRecipeBuilder requires(Ingredient ingredient, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.ingredients.add(ingredient);
        }

        return this;
    }

    public GrindstoneRecipeBuilder unlockedBy(String string, CriterionTriggerInstance criterionTriggerInstance) {
        this.advancement.addCriterion(string, criterionTriggerInstance);
        return this;
    }

    public GrindstoneRecipeBuilder group(String string) {
        this.group = string;
        return this;
    }

    public GrindstoneRecipeBuilder experience(int experience) {
        this.experience = experience;
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        this.save(consumer, Registry.ITEM.getKey(this.result));
    }

    public void save(Consumer<FinishedRecipe> consumer, String string) {
        ResourceLocation resourceLocation = Registry.ITEM.getKey(this.result);
        if (new ResourceLocation(string).equals(resourceLocation)) {
            throw new IllegalStateException("Grindstone Recipe " + string + " should remove its 'save' argument");
        } else {
            this.save(consumer, new ResourceLocation(string));
        }
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation resourceLocation) {
        this.ensureValid(resourceLocation);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation)).rewards(AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR);
        consumer.accept(new GrindstoneRecipeBuilder.Result(resourceLocation, this.result, this.count, this.group == null ? "" : this.group, experience, this.ingredients, this.advancement, new ResourceLocation(resourceLocation.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + resourceLocation.getPath())));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(ResourceLocation id) {
        if (this.advancement.getCriteria().isEmpty())
            throw new IllegalStateException("No way of obtaining recipe " + id);
    }

    public static class Result implements FinishedRecipe {

        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final String group;
        private final int experience;
        private final List<Ingredient> ingredients;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Item result, int count, String group, int experience, List<Ingredient> ingredients, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.count = count;
            this.group = group;
            this.experience = experience;
            this.ingredients = ingredients;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty())
                json.addProperty("group", this.group);
            if (this.experience != 0)
                json.addProperty("experience", this.experience);

            JsonArray jsonArray = new JsonArray();

            for (Ingredient ingredient : this.ingredients) {
                jsonArray.add(ingredient.toJson());
            }

            json.add("ingredients", jsonArray);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", Registry.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                jsonObject.addProperty("count", this.count);
            }

            json.add("result", jsonObject);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return PollenRecipeTypes.GRINDSTONE.get();
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
