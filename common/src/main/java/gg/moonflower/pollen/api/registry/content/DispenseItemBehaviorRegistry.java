package gg.moonflower.pollen.api.registry.content;

import gg.moonflower.pollen.core.mixin.DispenserBlockAccessor;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.function.BiPredicate;

/**
 * @author Jackson
 * @since 1.4.0
 */
public final class DispenseItemBehaviorRegistry {

    private DispenseItemBehaviorRegistry() {
    }

    /**
     * Registers a dispenser behavior and overrides any currently existing ones.
     *
     * @param item     The item to register the behavior to
     * @param behavior The dispenser behavior
     */
    public static synchronized void register(ItemLike item, DispenseItemBehavior behavior) {
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
    public static synchronized void register(ItemLike item, BiPredicate<BlockSource, ItemStack> condition, DispenseItemBehavior behavior) {
        DispenseItemBehavior old = DispenserBlockAccessor.getDispenserRegistry().get(item.asItem());
        DispenseItemBehaviorRegistry.register(item, (source, stack) -> condition.test(source, stack) || old == null ? behavior.dispense(source, stack) : old.dispense(source, stack));
    }
}
