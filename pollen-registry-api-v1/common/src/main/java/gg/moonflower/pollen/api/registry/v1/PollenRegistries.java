package gg.moonflower.pollen.api.registry.v1;

import gg.moonflower.pollen.api.registry.v1.entity.PollinatedBoatType;
import gg.moonflower.pollen.impl.PollenRegistryApiInitializer;
import net.minecraft.resources.ResourceLocation;

/**
 * Contains special custom Pollen registries.
 *
 * @since 1.4.0
 */
public interface PollenRegistries {

    PollinatedRegistry<PollinatedBoatType> BOAT_TYPE_REGISTRY = PollinatedRegistry.createSimple(new ResourceLocation(PollenRegistryApiInitializer.MOD_ID, "boat_type"));

}
