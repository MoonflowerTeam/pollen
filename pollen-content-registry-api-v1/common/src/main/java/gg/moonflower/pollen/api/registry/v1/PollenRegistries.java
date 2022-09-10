package gg.moonflower.pollen.api.registry.v1;

import gg.moonflower.pollen.api.registry.v1.entity.PollinatedBoatType;
import gg.moonflower.pollen.impl.PollenRegistryApiInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Contains special custom Pollen registries.
 *
 * @since 1.4.0
 */
public interface PollenRegistries {

    PollinatedRegistry<PollinatedBoatType> BOAT_TYPE_REGISTRY = PollinatedRegistry.createSimple(new ResourceLocation(PollenRegistryApiInitializer.MOD_ID, "boat_type"));


    /**
     * Creates a {@link PollinatedRegistry} for registering blocks and item blocks. The mod id from the item registry is used as the id for the block registry.
     *
     * @param itemRegistry The registry to add items to
     * @return A specialized block registry that can register items
     */
    static PollinatedBlockRegistry createBlock(PollinatedRegistry<Item> itemRegistry) {
        return new PollinatedBlockRegistry(create(Registry.BLOCK, itemRegistry.getModId()), itemRegistry);
    }

    /**
     * Creates a {@link PollinatedRegistry} for registering fluids.
     *
     * @param domain The domain of the mod
     * @return A specialized fluid registry that can fully handle fluids
     */
    static PollinatedFluidRegistry createFluid(String domain) {
        return new PollinatedFluidRegistry(create(Registry.FLUID, domain));
    }

    /**
     * Creates a {@link PollinatedRegistry} for registering entities and Ai. Ai registries are automatically set to use the domain provided.
     *
     * @param domain The domain of the mod
     * @return A specialized entity registry that can also register Ai
     */
    static PollinatedEntityRegistry createEntity(String domain) {
        return new PollinatedEntityRegistry(create(Registry.ENTITY_TYPE, domain));
    }
}
