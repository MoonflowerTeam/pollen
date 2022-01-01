package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.registry.StrippingRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "useOn", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    public void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState state = StrippingRegistry.getStrippedState(level.getBlockState(blockPos));
        if (state != null) {
            Player player = context.getPlayer();
            level.playSound(player, blockPos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!level.isClientSide()) {
                level.setBlock(blockPos, state, 11);
                if (player != null)
                    context.getItemInHand().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
            }

            cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide()));
        }
    }
}
