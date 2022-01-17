package gg.moonflower.pollen.core.forge.compat.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.api.crafting.grindstone.PollenGrindstoneRecipe;
import gg.moonflower.pollen.core.Pollen;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class PollenGrindstoneCategory implements IRecipeCategory<PollenGrindstoneRecipe> {

    private final IDrawable background;
    private final IDrawable icon;
    private final LoadingCache<PollenGrindstoneRecipe, DisplayData> cachedDisplayData;

    public PollenGrindstoneCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(new ResourceLocation("textures/gui/container/grindstone.png"), 30, 15, 116, 56).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(Blocks.GRINDSTONE));
        this.cachedDisplayData = CacheBuilder.newBuilder().maximumSize(25).build(new CacheLoader<PollenGrindstoneRecipe, DisplayData>() {
            @Override
            public DisplayData load(PollenGrindstoneRecipe key) {
                return new DisplayData();
            }
        });
    }

    @Override
    public ResourceLocation getUid() {
        return PollenJeiPlugin.GRINDSTONE_CATEGORY_ID;
    }

    @Override
    public Class<? extends PollenGrindstoneRecipe> getRecipeClass() {
        return PollenGrindstoneRecipe.class;
    }

    @Override
    @Deprecated
    public String getTitle() {
        return this.getTitleAsTextComponent().getString();
    }

    @Override
    public Component getTitleAsTextComponent() {
        return Blocks.GRINDSTONE.getName();
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(PollenGrindstoneRecipe recipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, recipe.getIngredients().stream().map(ingredient -> Arrays.asList(ingredient.getItems())).collect(Collectors.toList()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PollenGrindstoneRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 18, 3);
        guiItemStacks.init(1, true, 18, 24);
        guiItemStacks.init(2, false, 98, 18);

        guiItemStacks.set(ingredients);

        this.cachedDisplayData.getUnchecked(recipe).currentIngredients = guiItemStacks.getGuiIngredients();
    }

    private static int getExperienceFromItem(ItemStack stack) {
        int i = 0;
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);

        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
            Enchantment enchantment = entry.getKey();
            Integer integer = entry.getValue();
            if (!enchantment.isCurse())
                i += enchantment.getMinCost(integer);
        }

        return i;
    }

    @Override
    public void draw(PollenGrindstoneRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
        int experience = recipe.getResultExperience();

        if (experience == -1) {
            DisplayData displayData = this.cachedDisplayData.getUnchecked(recipe);
            if (displayData.currentIngredients == null)
                return;

            ItemStack newTopStack = displayData.currentIngredients.get(0).getDisplayedIngredient();
            ItemStack newBottomStack = displayData.currentIngredients.get(1).getDisplayedIngredient();
            if(newBottomStack == null)
                newBottomStack = ItemStack.EMPTY;
            if (newTopStack == null)
                return;

            experience = displayData.lastExperience;
            if (displayData.lastTopStack == null || !ItemStack.matches(newBottomStack, displayData.lastBottomStack) || !ItemStack.matches(displayData.lastTopStack, newTopStack)) {
                experience = getExperienceFromItem(newTopStack) + getExperienceFromItem(newBottomStack);
                displayData.setLast(newTopStack, newBottomStack, experience);
            }
        }

        if (experience > 0) {
            TranslatableComponent experienceString = new TranslatableComponent("gui.jei.category." + Pollen.MOD_ID + ".grindstone.experience", (int) Math.ceil((double) experience / 2.0), experience);
            Font font = Minecraft.getInstance().font;
            font.draw(matrixStack, experienceString, background.getWidth() - font.width(experienceString), 0, 0xFF808080);
        }
    }

    @Override
    public boolean isHandled(PollenGrindstoneRecipe recipe) {
        return !recipe.isSpecial();
    }

    private static class DisplayData {

        @Nullable
        private Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients;
        @Nullable
        private ItemStack lastTopStack;
        @Nullable
        private ItemStack lastBottomStack;
        private int lastExperience;

        public void setLast(ItemStack topStack, ItemStack bottomStack, int experience) {
            this.lastTopStack = topStack;
            this.lastBottomStack = bottomStack;
            this.lastExperience = experience;
        }
    }
}
