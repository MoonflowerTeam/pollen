package gg.moonflower.pollen.api.registry.content.v1;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.world.level.ItemLike;

/**
 * Registers composting probabilities to specific items.
 *
 * @author Ocelot
 * @since 2.0.0
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
        Pollen.expect();
    }
}
