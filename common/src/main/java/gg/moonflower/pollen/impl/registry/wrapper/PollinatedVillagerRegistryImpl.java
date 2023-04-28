package gg.moonflower.pollen.impl.registry.wrapper;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import gg.moonflower.pollen.api.registry.wrapper.v1.PollinatedVillagerRegistry;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.impl.mixin.PoiTypesAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedVillagerRegistryImpl extends PollinatedRegistryImpl<VillagerProfession> implements PollinatedVillagerRegistry {

    @ApiStatus.Internal
    @ExpectPlatform
    public static void registerPoiStates(Holder<PoiType> holder, Set<BlockState> states) {
        Pollen.expect();
    }

    private final DeferredRegister<PoiType> poiTypeRegistry;

    public PollinatedVillagerRegistryImpl(DeferredRegister<VillagerProfession> villagerProfessionRegistry) {
        super(villagerProfessionRegistry);
        this.poiTypeRegistry = DeferredRegister.create(this.getModId(), Registry.POINT_OF_INTEREST_TYPE_REGISTRY);
    }

    @Override
    public void register() {
        super.register();
        this.poiTypeRegistry.register();
    }

    public RegistrySupplier<PoiType> registerPoiType(String id, Supplier<PoiType> supplier) {
        RegistrySupplier<PoiType> poiType = this.poiTypeRegistry.register(id, supplier);
        poiType.listen(entry -> {
            Holder<PoiType> typeHolder = Registry.POINT_OF_INTEREST_TYPE.getHolderOrThrow(ResourceKey.create(Registry.POINT_OF_INTEREST_TYPE_REGISTRY, poiType.getId()));
            PollinatedVillagerRegistryImpl.registerPoiStates(typeHolder, entry.matchingStates());
        });
        return poiType;
    }

    @Override
    public DeferredRegister<PoiType> getPoiTypeRegistry() {
        return poiTypeRegistry;
    }
}
