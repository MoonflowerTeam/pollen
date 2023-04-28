package gg.moonflower.pollen.impl.registry.wrapper.forge;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class PollinatedVillagerRegistryImplImpl {
    public static void registerPoiStates(Holder<PoiType> holder, Set<BlockState> states) {
        // Do nothing, forge handles this for us already.
    }
}
