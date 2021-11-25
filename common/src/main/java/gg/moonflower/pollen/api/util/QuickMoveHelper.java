package gg.moonflower.pollen.api.util;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Quick moves items from one slot to another in an easier way than manually checking for slot indices.</p>
 * <p>Actions are performed from top to bottom, so overlaps between actions will always prioritize the one added first.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class QuickMoveHelper {
    private final List<Action> actions;

    public QuickMoveHelper() {
        this.actions = new ArrayList<>();
    }

    /**
     * Custom implementation of {@link AbstractContainerMenu#moveItemStackTo(ItemStack, int, int, boolean)} that respects slot restrictions.
     */
    private static boolean mergeItemStack(AbstractContainerMenu menu, ItemStack stack, int startIndex, int endIndex, boolean reverse) {
        boolean flag = false;
        int i = startIndex;
        if (reverse) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (reverse) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = menu.getSlot(i);
                ItemStack itemstack = slot.getItem();
                if (slot.mayPlace(stack) && !itemstack.isEmpty() && AbstractContainerMenu.consideredTheSameItem(stack, itemstack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(stack), stack.getMaxStackSize());
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.setChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.setChanged();
                        flag = true;
                    }
                }

                if (reverse) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverse) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while (true) {
                if (reverse) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = menu.getSlot(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(stack)) {
                    if (stack.getCount() > slot1.getMaxStackSize(stack)) {
                        slot1.set(stack.split(slot1.getMaxStackSize(stack)));
                    } else {
                        slot1.set(stack.split(stack.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (reverse) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    /**
     * Adds a new action to the move helper.
     *
     * @param fromStart The slot index to move items from
     * @param fromSize  The amount of slots to include in the starting area
     * @param toStart   The slot index to move items to
     * @param toSize    The amount of slots to include in the ending area
     * @param reverse   Whether to start from the last slot of the to area
     */
    public QuickMoveHelper add(int fromStart, int fromSize, int toStart, int toSize, boolean reverse) {
        this.actions.add(new Action(fromStart, fromSize, toStart, toSize, reverse));
        return this;
    }

    /**
     * Performs a quick move for the specified menu from the specified slot.
     *
     * @param menu The menu to quick move for
     * @param slot The slot to move from
     * @return The remaining items after the move
     */
    public ItemStack performAction(AbstractContainerMenu menu, int slot) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = menu.getSlot(slot);
        if (lv2 != null && lv2.hasItem()) {
            ItemStack lv3 = lv2.getItem();
            lv = lv3.copy();

            for (Action action : this.actions) {
                if (slot < action.fromStart || slot >= action.fromEnd)
                    continue;
                if (!mergeItemStack(menu, lv3, action.toStart, action.toEnd, action.reverse))
                    return ItemStack.EMPTY;
            }

            if (lv3.isEmpty()) {
                lv2.set(ItemStack.EMPTY);
            } else {
                lv2.setChanged();
            }

            if (lv3.getCount() == lv.getCount()) {
                return ItemStack.EMPTY;
            }
        }

        return lv;
    }

    /**
     * <p>An action that can take place for stacks.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Action {
        private final int fromStart;
        private final int fromEnd;
        private final int toStart;
        private final int toEnd;
        private final boolean reverse;

        public Action(int fromStart, int fromSize, int toStart, int toSize, boolean reverse) {
            this.fromStart = fromStart;
            this.fromEnd = fromStart + fromSize;
            this.toStart = toStart;
            this.toEnd = toStart + toSize;
            this.reverse = reverse;
        }
    }
}