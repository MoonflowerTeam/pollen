package gg.moonflower.pollen.api.registry.content;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * @author Ocelot
 * @since 1.4.0
 */
public class FurnaceFuelRegistry {

    /**
     * Registers a burn time for the specified item.
     *
     * @param item      The item to set burn ticks for
     * @param burnTicks The amount of ticks the item can burn for
     */
    @ExpectPlatform
    public static void register(ItemLike item, int burnTicks) {
        Platform.error();
    }
}
