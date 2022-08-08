package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.FabricHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

    @Inject(method = "growCrop", at = @At("HEAD"), cancellable = true)
    private static void growCrop(ItemStack stack, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        int processed = FabricHooks.processBonemeal(level, pos, level.getBlockState(pos), stack);
        if (processed != 0)
            cir.setReturnValue(processed > 0);
    }
}
