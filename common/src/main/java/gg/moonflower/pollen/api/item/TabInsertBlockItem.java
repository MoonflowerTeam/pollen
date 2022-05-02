package gg.moonflower.pollen.api.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.Predicate;

// TODO Rename class to a better name in 2.0.0

/**
 * A block item which inserts into the creative tab based on a filter.
 * <p>Unlike {@link BlockItem}, this class ignores {@link Block#fillItemCategory(CreativeModeTab, NonNullList)}
 *
 * @author Jackson
 * @since 1.5.0
 */
public class TabInsertBlockItem extends BlockItem {

    private final Predicate<ItemStack> filter;
    private final boolean insertBefore;

    public TabInsertBlockItem(Item insertAfter, Block block, Properties properties) {
        this(stack -> stack.getItem() == insertAfter, false, block, properties);
    }

    public TabInsertBlockItem(Class<Item> insertAfterClass, Block block, Properties properties) {
        this(stack -> insertAfterClass.isInstance(stack.getItem()), false, block, properties);
    }

    public TabInsertBlockItem(Predicate<ItemStack> filter, Block block, Properties properties) {
        this(filter, false, block, properties);
    }

    public TabInsertBlockItem(Predicate<ItemStack> filter, boolean insertBefore, Block block, Properties properties) {
        super(block, properties);
        this.filter = filter;
        this.insertBefore = insertBefore;
    }

    @Override
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
        if (this.allowdedIn(category))
            TabFiller.insert(new ItemStack(this), this.insertBefore, items, this.filter);
    }
}