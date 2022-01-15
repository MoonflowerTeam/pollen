package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.crafting.PollenRecipeTypes;
import gg.moonflower.pollen.api.crafting.grindstone.PollenGrindstoneRecipe;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.core.extensions.GrindstoneMenuExtension;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin extends AbstractContainerMenu implements GrindstoneMenuExtension {

    @Shadow
    @Final
    Container repairSlots;

    @Shadow
    @Final
    private Container resultSlots;

    @Nullable
    @Unique
    private PollenGrindstoneRecipe recipe;

    private GrindstoneMenuMixin(@Nullable MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @Inject(method = "createResult", at = @At("RETURN"))
    public void modifyAllowed(CallbackInfo ci) {
        this.recipe = null;
        if (this.resultSlots.getItem(0).isEmpty()) {
            Platform.getRecipeManager().ifPresent(recipeManager -> {
                Optional<PollenGrindstoneRecipe> optional = recipeManager.getRecipeFor(PollenRecipeTypes.GRINDSTONE_TYPE.get(), this.repairSlots, null);
                optional.ifPresent(recipe -> {
                    this.recipe = recipe;
                    this.resultSlots.setItem(0, recipe.assemble(this.repairSlots));
                });
                this.broadcastChanges();
            });
        }
    }

    @Override
    public void pollen_craft(Player player) {
        NonNullList<ItemStack> nonNullList = player.level.getRecipeManager().getRemainingItemsFor(PollenRecipeTypes.GRINDSTONE_TYPE.get(), this.repairSlots, player.level);

        for (int i = 0; i < nonNullList.size(); ++i) {
            ItemStack itemStack = this.repairSlots.getItem(i);
            ItemStack itemStack2 = nonNullList.get(i);
            if (!itemStack.isEmpty()) {
                this.repairSlots.removeItem(i, 1);
                itemStack = this.repairSlots.getItem(i);
            }

            if (!itemStack2.isEmpty()) {
                if (itemStack.isEmpty()) {
                    this.repairSlots.setItem(i, itemStack2);
                } else if (ItemStack.isSame(itemStack, itemStack2) && ItemStack.tagMatches(itemStack, itemStack2)) {
                    itemStack2.grow(itemStack.getCount());
                    this.repairSlots.setItem(i, itemStack2);
                } else if (!player.getInventory().add(itemStack2)) {
                    player.drop(itemStack2, false);
                }
            }
        }
    }

    @Override
    public boolean pollen_hasRecipeExperience() {
        return this.recipe != null;
    }

    @Override
    public int pollen_getResultExperience() {
        return Objects.requireNonNull(this.recipe).getResultExperience();
    }
}
