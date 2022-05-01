package gg.moonflower.pollen.api.registry.content.fabric;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FlammabilityRegistryImpl {

    public static void register(Block fireBlock, Block block, int encouragement, int flammability) {
        FlammableBlockRegistry.getInstance(fireBlock).add(block, encouragement, flammability);
    }
}
