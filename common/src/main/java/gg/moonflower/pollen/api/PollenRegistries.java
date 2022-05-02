package gg.moonflower.pollen.api;

import gg.moonflower.pollen.api.entity.PollinatedBoatType;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;

/**
 * @since 1.4.0
 */
public final class PollenRegistries {

    public static final PollinatedRegistry<PollinatedBoatType> BOAT_TYPE_REGISTRY = PollinatedRegistry.createSimple(new ResourceLocation(Pollen.MOD_ID, "boat_type"));

    private PollenRegistries(){
    }
}
