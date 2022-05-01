package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DispenserBlock.class)
public interface DispenserBlockAccessor {

    @Accessor("DISPENSER_REGISTRY")
    static Map<Item, DispenseItemBehavior> getDispenserRegistry() {
        return Platform.error();
    }
}
