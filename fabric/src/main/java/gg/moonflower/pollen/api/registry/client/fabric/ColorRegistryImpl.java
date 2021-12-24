package gg.moonflower.pollen.api.registry.client.fabric;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ColorRegistryImpl {
    public static void register(ItemColor itemColor, ItemLike... items) {
        ColorProviderRegistry.ITEM.register(itemColor, items);
    }

    public static void register(BlockColor blockColor, Block... blocks) {
        ColorProviderRegistry.BLOCK.register(blockColor, blocks);
    }
}
