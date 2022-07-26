package gg.moonflower.pollen.core.compat.jei;

import com.google.common.collect.ImmutableSet;
import gg.moonflower.pollen.api.crafting.PollenBrewingRecipe;
import gg.moonflower.pollen.api.crafting.PollenRecipeTypes;
import gg.moonflower.pollen.api.crafting.grindstone.PollenGrindstoneRecipe;
import gg.moonflower.pollen.core.Pollen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.screens.inventory.GrindstoneScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JeiPlugin
@ApiStatus.Internal
public class PollenJeiPlugin implements IModPlugin {

    public static final ResourceLocation PLUGIN_ID = new ResourceLocation(Pollen.MOD_ID, "vanilla");
    public static final RecipeType<PollenGrindstoneRecipe> GRINDSTONE_CATEGORY_ID = RecipeType.create(Pollen.MOD_ID, "grindstone", PollenGrindstoneRecipe.class);
    @Nullable
    private PollenGrindstoneCategory grindstoneCategory;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(this.grindstoneCategory = new PollenGrindstoneCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Validate.notNull(this.grindstoneCategory, "grindstoneCategory");

        Set<Item> potions = ImmutableSet.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
        List<PollenBrewingRecipe> recipes = PollenRecipeMaker.getRecipes(null, PollenRecipeTypes.BREWING_TYPE.get());
        for (PollenBrewingRecipe recipe : recipes) {
            List<ItemStack> ingredients = Arrays.asList(recipe.getIngredient().getItems());
            registration.addRecipes(RecipeTypes.BREWING, potions.stream().map(item -> {
                ItemStack input = new ItemStack(item);
                ItemStack result = new ItemStack(item);
                PotionUtils.setPotion(input, recipe.getFrom());
                PotionUtils.setPotion(result, recipe.getResult());
                return registration.getVanillaRecipeFactory().createBrewingRecipe(ingredients, input, result);
            }).collect(Collectors.toList()));
        }

        registration.addRecipes(GRINDSTONE_CATEGORY_ID, PollenRecipeMaker.getGrindstoneRecipes(this.grindstoneCategory, registration.getIngredientManager()));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(GrindstoneScreen.class, 95, 34, 22, 15, GRINDSTONE_CATEGORY_ID);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(GrindstoneMenu.class, MenuType.GRINDSTONE, GRINDSTONE_CATEGORY_ID, 0, 2, 3, 36);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.GRINDSTONE), GRINDSTONE_CATEGORY_ID);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }
}
