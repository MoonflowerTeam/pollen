package gg.moonflower.pollen.common.events;

import gg.moonflower.pollen.api.event.EventResult;
import gg.moonflower.pollen.api.event.events.world.WorldEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.Random;

@ApiStatus.Internal
public final class FabricHooks {

    private FabricHooks() {
    }

    public static int onApplyBonemeal(Level level, BlockPos pos, BlockState state, ItemStack stack) {
        EventResult result = WorldEvents.BONEMEAL.invoker().bonemeal(level, pos, state, stack);
        if (result.isFalse()) return -1;
        if (result == EventResult.ALLOW) {
            if (!level.isClientSide)
                stack.shrink(1);
            return 1;
        }
        return 0;
    }
}
