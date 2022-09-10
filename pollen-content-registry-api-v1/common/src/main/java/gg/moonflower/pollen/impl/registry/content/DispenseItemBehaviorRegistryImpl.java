package gg.moonflower.pollen.impl.registry.content;

import gg.moonflower.pollen.mixin.DispenserBlockAccessor;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiPredicate;

/**
 * @author Jackson
 */
@ApiStatus.Internal
public final class DispenseItemBehaviorRegistryImpl {

    private DispenseItemBehaviorRegistryImpl() {
    }

    public static synchronized void register(ItemLike item, DispenseItemBehavior behavior) {
        DispenserBlock.registerBehavior(item, behavior);
    }

    public static synchronized void register(ItemLike item, BiPredicate<BlockSource, ItemStack> condition, DispenseItemBehavior behavior) {
        DispenseItemBehavior old = DispenserBlockAccessor.getDispenserRegistry().get(item.asItem());
        DispenseItemBehaviorRegistryImpl.register(item, (source, stack) -> condition.test(source, stack) || old == null ? behavior.dispense(source, stack) : old.dispense(source, stack));
    }
}
