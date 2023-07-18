package gg.moonflower.pollen.core.mixin.fabric.registry.content;

import gg.moonflower.pollen.api.registry.content.v1.FlatteningRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/context/UseOnContext;getPlayer()Lnet/minecraft/world/entity/player/Player;"), cancellable = true)
    public void captureUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = FlatteningRegistry.getFlattenedState(level.getBlockState(pos));
        if (state != null && level.getBlockState(pos.above()).isAir()) {
            Player player = context.getPlayer();
            level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!level.isClientSide()) {
                level.setBlock(pos, state, 11);
                if (player != null) {
                    context.getItemInHand().hurtAndBreak(1, player, playerx -> playerx.broadcastBreakEvent(context.getHand()));
                }
            }

            cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide()));
        }
    }
}
