package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public final class ColorRegistry {

    private ColorRegistry() {
    }

    @ExpectPlatform
    public static void register(ItemColor itemColor, ItemLike... items) {
        Platform.error();
    }

    @ExpectPlatform
    public static void register(BlockColor blockColor, Block... blocks) {
        Platform.error();
    }
}
