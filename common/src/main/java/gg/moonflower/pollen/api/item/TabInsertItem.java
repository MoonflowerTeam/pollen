package gg.moonflower.pollen.api.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/**
 * An item which inserts into the creative tab based on a filter.
 *
 * @author Jackson
 * @since 1.4.0
 */
public class TabInsertItem extends Item {

    private final Predicate<ItemStack> filter;
    private final boolean insertBefore;

    public TabInsertItem(Item insertAfter, Properties properties) {
        this(stack -> stack.getItem() == insertAfter, false, properties);
    }

    public TabInsertItem(Class<Item> insertAfterClass, Properties properties) {
        this(stack -> insertAfterClass.isInstance(stack.getItem()), false, properties);
    }

    public TabInsertItem(Predicate<ItemStack> filter, Properties properties) {
        this(filter, false, properties);
    }

    public TabInsertItem(Predicate<ItemStack> filter, boolean insertBefore, Properties properties) {
        super(properties);
        this.filter = filter;
        this.insertBefore = insertBefore;
    }

    @Override
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
        if (this.allowedIn(category))
            TabFiller.insert(new ItemStack(this), this.insertBefore, items, this.filter);
    }
}
