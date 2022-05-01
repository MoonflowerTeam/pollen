package gg.moonflower.pollen.api.block;

import gg.moonflower.pollen.api.item.TabFiller;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.Predicate;

/**
 * A block which inserts into the creative tab based on a filter.
 *
 * @author Jackson
 * @since 1.4.0
 */
public class TabInsertBlock extends Block {

    private final Predicate<ItemStack> filter;
    private final boolean insertBefore;

    public TabInsertBlock(Item insertAfter, Properties properties) {
        this(stack -> stack.getItem() == insertAfter, false, properties);
    }

    public TabInsertBlock(Class<Item> insertAfterClass, Properties properties) {
        this(stack -> insertAfterClass.isInstance(stack.getItem()), false, properties);
    }

    public TabInsertBlock(Predicate<ItemStack> filter, Properties properties) {
        this(filter, false, properties);
    }

    public TabInsertBlock(Predicate<ItemStack> filter, boolean insertBefore, Properties properties) {
        super(properties);
        this.filter = filter;
        this.insertBefore = insertBefore;
    }

    @Override
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
        TabFiller.insert(new ItemStack(this), this.insertBefore, items, this.filter);
    }
}
