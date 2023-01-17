package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.events.world.WorldEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

    @Inject(method = "growCrop", at = @At("HEAD"), cancellable = true)
    private static void growCrop(ItemStack stack, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        int processed = processBonemeal(level, pos, level.getBlockState(pos), stack);
        if (processed != 0)
            cir.setReturnValue(processed > 0);
    }

    @Unique
    private static int processBonemeal(Level level, BlockPos pos, BlockState state, ItemStack stack) {
        InteractionResult result = WorldEvents.BONEMEAL.invoker().bonemeal(level, pos, state, stack);
        if (result == InteractionResult.FAIL) {
            return -1;
        } else if (result == InteractionResult.SUCCESS) {
            if (!level.isClientSide) {
                stack.shrink(1);
            }
            return 1;
        } else {
            return 0;
        }
    }
}