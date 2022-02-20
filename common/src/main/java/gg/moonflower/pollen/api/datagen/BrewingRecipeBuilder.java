package gg.moonflower.pollen.api.datagen;

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
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class BrewingRecipeBuilder {

    private final Potion result;
    private Potion from;
    private Ingredient ingredient;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private String group;

    public BrewingRecipeBuilder(Potion result) {
        this.result = result;
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static BrewingRecipeBuilder shaped(Potion result) {
        return new BrewingRecipeBuilder(result);
    }

    public BrewingRecipeBuilder requires(Potion from, Ingredient ingredient) {
        this.from = from;
        this.ingredient = ingredient;
        return this;
    }

    public BrewingRecipeBuilder unlockedBy(String string, CriterionTriggerInstance arg) {
        this.advancement.addCriterion(string, arg);
        return this;
    }

    public BrewingRecipeBuilder group(String string) {
        this.group = string;
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        this.save(consumer, Registry.POTION.getKey(this.result));
    }

    public void save(Consumer<FinishedRecipe> consumer, String string) {
        ResourceLocation key = Registry.POTION.getKey(this.result);
        if (new ResourceLocation(string).equals(key)) {
            throw new IllegalStateException("Shaped Recipe " + string + " should remove its 'save' argument");
        } else {
            this.save(consumer, new ResourceLocation(string));
        }
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation arg) {
        this.ensureValid(arg);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(arg)).rewards(AdvancementRewards.Builder.recipe(arg)).requirements(RequirementsStrategy.OR);
        consumer.accept(new Result(arg, this.result, this.group == null ? "" : this.group, this.from, this.ingredient, this.advancement, new ResourceLocation(arg.getNamespace(), "recipes/brewing/" + arg.getPath())));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(ResourceLocation id) {
        if (this.from == null)
            throw new IllegalStateException("No source potion is defined for brewing recipe " + id + "!");
        if (this.ingredient == null || this.ingredient.isEmpty())
            throw new IllegalStateException("No ingredient is defined for brewing recipe " + id + "!");
    }

    public static class Result implements FinishedRecipe {

        private final ResourceLocation id;
        private final String group;
        private final Potion from;
        private final Ingredient ingredient;
        private final Potion result;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Potion result, String group, Potion from, Ingredient ingredient, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.group = group;
            this.from = from;
            this.ingredient = ingredient;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty())
                json.addProperty("group", this.group);
            json.addProperty("from", Registry.POTION.getKey(this.from).toString());
            json.add("ingredient", this.ingredient.toJson());
            json.addProperty("result", Registry.POTION.getKey(this.result).toString());
        }

        @Override
        public RecipeSerializer<?> getType() {
            return PollenRecipeTypes.BREWING.get();
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
