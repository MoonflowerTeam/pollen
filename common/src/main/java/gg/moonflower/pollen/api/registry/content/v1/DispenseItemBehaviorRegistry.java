package gg.moonflower.pollen.api.registry.content.v1;

import gg.moonflower.pollen.impl.mixin.DispenserBlockAccessor;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.function.BiPredicate;

/**
 * Allows adding extra dispenser behavior based on specific conditions.
 *
 * @author Jackson
 * @since 2.0.0
 */
public interface DispenseItemBehaviorRegistry {

    /**
     * Registers a dispenser behavior and overrides any currently existing ones.
     *
     * @param item     The item to register the behavior to
     * @param behavior The dispenser behavior
     */
    static void register(ItemLike item, DispenseItemBehavior behavior) {
        DispenserBlock.registerBehavior(item, behavior);
    }

    /**
     * Registers a dispenser behavior with a predicate for retaining previous dispense behaviors.
     * <p>{@param condition} should never always be true. If overriding is needed, call {@link DispenseItemBehaviorRegistry#register(ItemLike, DispenseItemBehavior)}.
     *
     * @param condition The condition to test for when dispensing. Used to determine whether to use the new or old dispense behavior
     * @param item      The item to register the behavior to
     * @param behavior  The dispense behavior
     */
    static void register(ItemLike item, BiPredicate<BlockSource, ItemStack> condition, DispenseItemBehavior behavior) {
        DispenseItemBehavior old = DispenserBlockAccessor.getDispenserRegistry().get(item.asItem());
        DispenseItemBehaviorRegistry.register(item, (source, stack) -> condition.test(source, stack) || old == null ? behavior.dispense(source, stack) : old.dispense(source, stack));
    }
}
