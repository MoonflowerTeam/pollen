package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.registry.content.forge.FurnaceFuelRegistryImpl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Mixin(ForgeHooks.class)
public class ForgeHooksMixin {

    @Inject(method = "getBurnTime", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true, remap = false)
    private static void modifyBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType, CallbackInfoReturnable<Integer> cir, Item item, int ret) {
        if (ret == -1 && FurnaceFuelRegistryImpl.hasBurnTime(item))
            cir.setReturnValue(FurnaceFuelRegistryImpl.getBurnTime(item));
    }
}
