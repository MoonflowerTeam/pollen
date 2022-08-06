package gg.moonflower.pollen.api.event;

import gg.moonflower.pollen.api.event.events.world.WorldEvents;
import gg.moonflower.pollen.common.events.context.ResultContextImpl;
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
        ResultContext resultContext = new ResultContextImpl();
        boolean event = WorldEvents.BONEMEAL.invoker().bonemeal(level, pos, state, stack, resultContext);
        if (!event) {
            return -1;
        } else if (resultContext.getResult() == EventResult.ALLOW) {
            if (!level.isClientSide) {
                stack.shrink(1);
            }

            return 1;
        } else {
            return 0;
        }
    }
}
