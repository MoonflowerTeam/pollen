package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.registry.content.forge.FurnaceFuelRegistryImpl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.extensions.IForgeItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public abstract class ItemMixin implements IForgeItem {

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        if (FurnaceFuelRegistryImpl.hasBurnTime((Item) (Object) this))
            return FurnaceFuelRegistryImpl.getBurnTime((Item) (Object) this);
        return IForgeItem.super.getBurnTime(itemStack, recipeType);
    }
}
