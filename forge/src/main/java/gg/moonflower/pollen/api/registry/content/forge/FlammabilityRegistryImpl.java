package gg.moonflower.pollen.api.registry.content.forge;

import gg.moonflower.pollen.core.mixin.forge.FireBlockAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FlammabilityRegistryImpl {

    public static synchronized void register(Block fireBlock, Block block, int encouragement, int flammability) {
        if (!(fireBlock instanceof FireBlock))
            throw new IllegalStateException("Block " + fireBlock + " is not an instance of FireBlock");
        ((FireBlockAccessor) fireBlock).invokeSetFlammable(block, encouragement, flammability);
    }
}
