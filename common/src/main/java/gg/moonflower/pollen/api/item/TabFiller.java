package gg.moonflower.pollen.api.item;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Helpers for inserting items into creative mode tabs.
 *
 * @author Ocelot
 * @since 1.4.0
 */
public class TabFiller {

    /**
     * Inserts the specified item stack into the items list.
     *
     * @param stack  The stack to insert
     * @param before Whether to insert the stack before the first item filtered or after the last item filtered
     * @param items  The list of items to insert into
     * @param filter The filter for items to insert around
     */
    public static void insert(ItemStack stack, boolean before, NonNullList<ItemStack> items, Predicate<ItemStack> filter) {
        if (items.stream().anyMatch(filter)) {
            Optional<ItemStack> optional = items.stream().filter(filter).max((a, b) ->
            {
                int valA = items.indexOf(a);
                int valB = items.indexOf(b);
                if (valA == -1 && valB == -1)
                    return 0;
                if (valA == -1)
                    return valB;
                if (valB == -1)
                    return valA;
                return before ? valB - valA : valA - valB;
            });
            if (optional.isPresent()) {
                items.add(items.indexOf(optional.get()) + (before ? 0 : 1), stack);
                return;
            }
        }
        items.add(stack);
    }

    /**
     * Inserts the specified item stack into the items list based on the registry name.
     *
     * @param stack  The stack to insert
     * @param before Whether to insert the stack before or after the found item
     * @param items  The list of items to insert into
     * @param filter The filter for items to insert around
     */
    public static void insertNamed(ItemStack stack, boolean before, NonNullList<ItemStack> items, Predicate<ItemStack> filter) {
        if (items.stream().anyMatch(filter)) {
            String itemName = Registry.ITEM.getKey(stack.getItem()).getPath();
            Optional<ItemStack> optional = items.stream().filter(filter).max((a, b) ->
            {
                int valA = itemName.compareToIgnoreCase(Registry.ITEM.getKey(a.getItem()).getPath());
                int valB = Registry.ITEM.getKey(b.getItem()).getPath().compareToIgnoreCase(itemName);
                return valB - valA;
            });
            if (optional.isPresent()) {
                items.add(items.indexOf(optional.get()) + (before ? 0 : 1), stack);
                return;
            }
        }
        items.add(stack);
    }
}
