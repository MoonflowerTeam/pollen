package gg.moonflower.pollen.api.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A registry to set how Dispensers and Droppers interact with blocks and items.
 *
 * @author Eltrutlikes
 * @author ebo2022
 */
public class DispenserBehaviorRegistry {

    /**
     * Registers basic behavior for the specified block and item.
     *
     * @param item        The item to register behavior for
     * @param block       The block the item will interact with
     * @param newBehavior The new dispenser behavior to register
     */
    public static void registerSimpleBehavior(ItemLike item, Block block, DispenseItemBehavior newBehavior) {
        DispenseItemBehavior oldBehavior = DispenserBlock.DISPENSER_REGISTRY.get(item);
        DispenserBlock.registerBehavior(item, (source, stack) -> {
            Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos pos = source.getPos().relative(dir);
            BlockState state = source.getLevel().getBlockState(pos);

            return state.is(block) ? newBehavior.dispense(source, stack) : oldBehavior.dispense(source, stack);
        });
    }

    /**
     * Registers behavior for the specified item.
     *
     * @param item     The item to register behavior for
     * @param behavior The dispenser behavior to register
     */
    public static void registerBehavior(ItemLike item, DispenseItemBehavior behavior) {
        DispenserBlock.registerBehavior(item, behavior);
    }
}
