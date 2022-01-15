package gg.moonflower.pollen.api.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class PollenBrewingRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final String group;
    private final Potion from;
    private final Ingredient ingredient;
    private final Potion result;

    public PollenBrewingRecipe(ResourceLocation id, String group, Potion from, Ingredient ingredient, Potion result) {
        this.id = id;
        this.group = group;
        this.from = from;
        this.ingredient = ingredient;
        this.result = result;
    }

    @ApiStatus.Internal
    public static PollenBrewingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
        String group = GsonHelper.getAsString(jsonObject, "group", "");
        Potion from = Registry.POTION.get(new ResourceLocation(GsonHelper.getAsString(jsonObject, "from")));
        Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
        Potion result = Registry.POTION.get(new ResourceLocation(GsonHelper.getAsString(jsonObject, "result")));
        return new PollenBrewingRecipe(resourceLocation, group, from, ingredient, result);
    }

    @ApiStatus.Internal
    public static PollenBrewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        String group = buf.readUtf();
        Potion from = Registry.POTION.get(buf.readResourceLocation());
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        Potion result = Registry.POTION.get(buf.readResourceLocation());
        return new PollenBrewingRecipe(id, group, from, ingredient, result);
    }

    @ApiStatus.Internal
    public static void toNetwork(FriendlyByteBuf buf, PollenBrewingRecipe recipe) {
        buf.writeUtf(recipe.group);
        buf.writeResourceLocation(Registry.POTION.getKey(recipe.from));
        recipe.ingredient.toNetwork(buf);
        buf.writeResourceLocation(Registry.POTION.getKey(recipe.result));
    }

    public Potion getFrom() {
        return from;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public Potion getResult() {
        return result;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w >= 1 && h >= 2;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
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
        return PollenRecipeTypes.BREWING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return PollenRecipeTypes.BREWING_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.ingredient);
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.BREWING_STAND);
    }
}
