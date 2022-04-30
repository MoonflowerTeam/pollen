package gg.moonflower.pollen.api.registry.forge;

import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CompostablesRegistryImpl {

    public static synchronized void register(ItemLike item, float probability) {
        ComposterBlock.COMPOSTABLES.put(item.asItem(), probability);
    }
}
