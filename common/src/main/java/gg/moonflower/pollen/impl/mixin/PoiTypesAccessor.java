package gg.moonflower.pollen.impl.mixin;

import gg.moonflower.pollen.core.Pollen;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

@Mixin(PoiTypes.class)
public interface PoiTypesAccessor {

    @Accessor("TYPE_BY_STATE")
    static Map<BlockState, Holder<PoiType>> getTypeByState() {
        return Pollen.expect();
    }

    @Accessor("ALL_STATES")
    static Set<BlockState> getAllStates() {
        return Pollen.expect();
    }
}
