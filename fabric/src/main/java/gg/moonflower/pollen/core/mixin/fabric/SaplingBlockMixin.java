package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.events.entity.SetTargetEvent;
import gg.moonflower.pollen.api.event.events.world.TreeGrowingEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SaplingBlock.class)
public class SaplingBlockMixin {

    @Inject(method = "advanceTree", at = @At("TAIL"))
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, Random rand, CallbackInfo ci) {
        TreeGrowingEvent.EVENT.invoker().onTreeGrowing(pos, rand, (LevelAccessor) level);
    }
}
