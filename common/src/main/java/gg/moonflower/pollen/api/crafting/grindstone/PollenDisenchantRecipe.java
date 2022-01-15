package gg.moonflower.pollen.api.crafting.grindstone;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.crafting.PollenRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Ocelot
 * @since 1.0.0
 */
public class PollenDisenchantRecipe implements PollenGrindstoneRecipe {

    private final ResourceLocation id;
    private final String group;
    private final Ingredient ingredient;
    private int resultExperience;

    public PollenDisenchantRecipe(ResourceLocation id, String group, Ingredient ingredient) {
        this.id = id;
        this.group = group;
        this.ingredient = ingredient;
    }

    @ApiStatus.Internal
    public static PollenDisenchantRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        String group = GsonHelper.getAsString(json, "group", "");
        Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
        return new PollenDisenchantRecipe(recipeId, group, ingredient);
    }

    @ApiStatus.Internal
    public static PollenDisenchantRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        String group = buf.readUtf();
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        return new PollenDisenchantRecipe(id, group, ingredient);
    }

    @ApiStatus.Internal
    public static void toNetwork(FriendlyByteBuf buf, PollenDisenchantRecipe recipe) {
        buf.writeUtf(recipe.group);
        recipe.ingredient.toNetwork(buf);
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack top = container.getItem(0);
        ItemStack bottom = container.getItem(1);
        return (top.isEmpty() ^ bottom.isEmpty()) && (this.ingredient.test(top) || this.ingredient.test(bottom));
    }

    @Override
    public ItemStack assemble(Container container) {
        ItemStack result = (container.getItem(0).isEmpty() ? container.getItem(1) : container.getItem(0)).copy();
        result.removeTagKey("Enchantments");
        result.removeTagKey("StoredEnchantments");

        int damage = result.getDamageValue();
        if (damage > 0) {
            result.setDamageValue(damage);
        } else {
            result.removeTagKey("Damage");
        }

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result).entrySet().stream().filter(enchantment -> !enchantment.getKey().isCurse()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Enchantment, Integer> curses = EnchantmentHelper.getEnchantments(result).entrySet().stream().filter(enchantment -> enchantment.getKey().isCurse()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        EnchantmentHelper.setEnchantments(curses, result);
        result.setRepairCost(0);
        if (result.getItem() == Items.ENCHANTED_BOOK && curses.size() == 0) {
            result = new ItemStack(Items.BOOK);
            if (result.hasCustomHoverName())
                result.setHoverName(result.getHoverName());
        }

        for (int i = 0; i < curses.size(); ++i)
            result.setRepairCost(AnvilMenu.calculateIncreasedRepairCost(result.getBaseRepairCost()));

        this.resultExperience = 0;
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            Integer integer = entry.getValue();
            if (!enchantment.isCurse())
                this.resultExperience += enchantment.getMinCost(integer);
        }

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w * h >= 1;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
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
        return PollenRecipeTypes.DISENCHANT.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.ingredient);
    }

    @Override
    public int getResultExperience() {
        return resultExperience;
    }
}
