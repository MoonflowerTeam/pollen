package gg.moonflower.pollen.api.event;

import gg.moonflower.pollen.api.event.events.world.WorldEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FabricHooks {

    private FabricHooks() {
    }

    public static int processBonemeal(Level level, BlockPos pos, BlockState state, ItemStack stack) {
        EventResult result = WorldEvents.BONEMEAL.invoker().bonemeal(level, pos, state, stack);
        if (result == EventResult.DENY) {
            return -1;
        } else if (result == EventResult.ALLOW) {
            if (!level.isClientSide) {
                stack.shrink(1);
            }
            return 1;
        } else {
            return 0;
        }
    }
}
