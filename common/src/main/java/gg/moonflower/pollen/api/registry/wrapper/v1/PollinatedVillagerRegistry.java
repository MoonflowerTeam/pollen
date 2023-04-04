package gg.moonflower.pollen.api.registry.wrapper.v1;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import gg.moonflower.pollen.impl.registry.wrapper.PollinatedVillagerRegistryImpl;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;

import java.util.function.Supplier;

public interface PollinatedVillagerRegistry extends PollinatedRegistry<VillagerProfession> {

    static PollinatedVillagerRegistry create(String modId) {
        return new PollinatedVillagerRegistryImpl(DeferredRegister.create(modId, Registry.VILLAGER_PROFESSION_REGISTRY));
    }

    /**
     * Registers a new point of interest type.
     *
     * @param id       The id of the point of interest
     * @param supplier The generator for new point of interest values
     * @return A new point of interest type
     */
    RegistrySupplier<PoiType> registerPoiType(String id, Supplier<PoiType> supplier);

    /**
     * @return The registry used to add point of interest types. This is automatically registered by this registry
     */
    DeferredRegister<PoiType> getPoiTypeRegistry();
}
