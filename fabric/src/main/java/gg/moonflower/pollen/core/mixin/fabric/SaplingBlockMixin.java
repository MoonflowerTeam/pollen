package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.PollinatedEventResult;
import gg.moonflower.pollen.api.event.events.world.WorldEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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

    @Inject(method = "advanceTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/grower/AbstractTreeGrower;growTree(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Ljava/util/Random;)Z", shift = At.Shift.BEFORE), cancellable = true)
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, Random rand, CallbackInfo ci) {
        PollinatedEventResult result = WorldEvents.TREE_GROWING.invoker().interaction((LevelAccessor) level, rand, pos);
        if (result == PollinatedEventResult.DENY)
            ci.cancel();
    }
}