package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.core.extensions.GrindstoneMenuExtension;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
public abstract class GrindstoneMenuResultSlotMixin extends Slot {

    @Shadow(aliases = "this$0")
    @Final
    GrindstoneMenu field_16780;

    private GrindstoneMenuResultSlotMixin(Container container, int i, int j, int k) {
        super(container, i, j, k);
    }

    @Inject(method = "onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.BEFORE, ordinal = 0), cancellable = true)
    public void onTake(Player player, ItemStack stack, CallbackInfo ci) {
        this.checkTakeAchievements(stack);
        ((GrindstoneMenuExtension) this.field_16780).pollen_craft(player);
        ci.cancel();
    }

    @Inject(method = "getExperienceFromItem(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    public void getExperienceFromItem(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        GrindstoneMenuExtension extension = (GrindstoneMenuExtension) this.field_16780;
        if (extension.pollen_hasRecipeExperience())
            cir.setReturnValue(extension.pollen_getResultExperience());
    }
}
