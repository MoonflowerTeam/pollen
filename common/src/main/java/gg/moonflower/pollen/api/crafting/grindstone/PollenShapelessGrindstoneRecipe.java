package gg.moonflower.pollen.api.crafting.grindstone;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.crafting.PollenRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class PollenShapelessGrindstoneRecipe implements PollenGrindstoneRecipe {

    private final ResourceLocation id;
    private final String group;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    private final int experience;

    public PollenShapelessGrindstoneRecipe(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> ingredients, int experience) {
        this.id = id;
        this.group = group;
        this.result = result;
        this.ingredients = NonNullList.withSize(2, Ingredient.EMPTY);
        for (int i = 0; i < Math.min(this.ingredients.size(), ingredients.size()); i++)
            this.ingredients.set(i, ingredients.get(i));
        this.experience = experience;
    }

    public static NonNullList<Ingredient> itemsFromJson(JsonArray json) {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for (int i = 0; i < json.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(json.get(i));
            if (!ingredient.isEmpty())
                ingredients.add(ingredient);
        }

        return ingredients;
    }

    @ApiStatus.Internal
    public static PollenShapelessGrindstoneRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        String group = GsonHelper.getAsString(json, "group", "");
        NonNullList<Ingredient> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));

        if (ingredients.isEmpty())
            throw new JsonParseException("No ingredients for grindstone recipe");
        if (ingredients.size() > 2)
            throw new JsonParseException("Too many ingredients for grindstone recipe");

        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        int experience = GsonHelper.getAsInt(json, "experience", 0);
        return new PollenShapelessGrindstoneRecipe(recipeId, group, result, ingredients, experience);
    }

    @ApiStatus.Internal
    public static PollenShapelessGrindstoneRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        String group = buf.readUtf();
        ItemStack result = buf.readItem();
        int experience = buf.readVarInt();
        NonNullList<Ingredient> ingredients = NonNullList.withSize(buf.readVarInt(), Ingredient.EMPTY);
        for (int i = 0; i < ingredients.size(); i++)
            ingredients.set(i, Ingredient.fromNetwork(buf));
        return new PollenShapelessGrindstoneRecipe(id, group, result, ingredients, experience);
    }

    @ApiStatus.Internal
    public static void toNetwork(FriendlyByteBuf buf, PollenShapelessGrindstoneRecipe recipe) {
        buf.writeUtf(recipe.group);
        buf.writeItem(recipe.result);
        buf.writeVarInt(recipe.experience);
        buf.writeVarInt(recipe.ingredients.size());
        for (Ingredient ingredient : recipe.ingredients)
            ingredient.toNetwork(buf);
    }

    @Override
    public boolean matches(Container container, Level level) {
        StackedContents stackedContents = new StackedContents();
        int i = 0;

        for (int j = 0; j < 2; ++j) {
            ItemStack itemStack = container.getItem(j);
            if (!itemStack.isEmpty()) {
                i++;
                stackedContents.accountStack(itemStack, 1);
            }
        }

        return i == this.ingredients.size() && stackedContents.canCraft(this, null);
    }

    @Override
    public ItemStack assemble(Container container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w * h >= this.ingredients.size();
    }

    @Override
    public ItemStack getResultItem() {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PollenRecipeTypes.GRINDSTONE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public int getResultExperience() {
        return experience;
    }
}
