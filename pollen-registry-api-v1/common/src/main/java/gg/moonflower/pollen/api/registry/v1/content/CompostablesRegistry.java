package gg.moonflower.pollen.api.registry.v1.content;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.base.platform.Platform;
import net.minecraft.world.level.ItemLike;

/**
 * @author Ocelot
 * @since 1.4.0
 */
public interface CompostablesRegistry {

    /**
     * Registers a composting probability for the specified item.
     *
     * @param item        The item to add probability to
     * @param probability The percentage chance of the item increasing the composter level
     */
    @ExpectPlatform
    static void register(ItemLike item, float probability) {
        Platform.error();
    }
}
