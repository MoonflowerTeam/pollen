package gg.moonflower.pollen.api.registry.v1.entity;

import gg.moonflower.pollen.api.registry.v1.item.PollinatedBoatItem;
import gg.moonflower.pollen.impl.entity.PollinatedBoat;
import net.minecraft.resources.ResourceLocation;

/**
 * A custom type of boat that can be summoned as a type of {@link PollinatedBoat}.
 *
 * @author Ocelot
 * @see PollinatedBoat
 * @see PollinatedBoatItem
 * @since 1.4.0
 */
public record PollinatedBoatType(ResourceLocation texture) {
}
