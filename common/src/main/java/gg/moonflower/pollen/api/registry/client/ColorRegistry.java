package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public final class ColorRegistry {

    private ColorRegistry() {
    }

    @SafeVarargs
    @ExpectPlatform
    public static void register(ItemColor itemColor, Supplier<? extends ItemLike>... items) {
        Platform.error();
    }

    @SafeVarargs
    @ExpectPlatform
    public static void register(BlockColor blockColor, Supplier<? extends Block>... blocks) {
        Platform.error();
    }
}
