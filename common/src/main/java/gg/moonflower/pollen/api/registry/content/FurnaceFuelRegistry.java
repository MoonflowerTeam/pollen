package gg.moonflower.pollen.api.registry.content;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.world.level.ItemLike;

/**
 * @author Ocelot
 * @since 1.4.0
 */
public final class FurnaceFuelRegistry {

    private FurnaceFuelRegistry() {
    }

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
