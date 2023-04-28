package gg.moonflower.pollen.impl.registry.wrapper.fabric;

import gg.moonflower.pollen.impl.mixin.PoiTypesAccessor;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class PollinatedVillagerRegistryImplImpl {
    @org.jetbrains.annotations.ApiStatus.Internal
    public static void registerPoiStates(Holder<PoiType> holder, Set<BlockState> states) {
        states.forEach(state -> {
            PoiTypesAccessor.getAllStates().add(state);
            PoiTypesAccessor.getTypeByState().put(state, holder);
        });
    }
}
