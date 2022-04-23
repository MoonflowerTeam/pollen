package gg.moonflower.pollen.api.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


/**
 * Ease-of-access methods for indexing the creative menu.
 *
 * @author Eltrutlikes
 * @author ebo2022
 */
public class GroupUtil {
    public static int getIndex(Item item, NonNullList<ItemStack> items) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static void fillItem(Item item, Item followItem, CreativeModeTab group, NonNullList<ItemStack> items) {
        int index = getIndex(followItem, items);
        if (index != -1) {
            items.add(++index, new ItemStack(item));
        } else {
            items.add(new ItemStack(item));
        }
    }
}
