package gg.moonflower.pollen.api.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;

/**
 * A sign item for modded signs which get sorted in the creative tab.
 *
 * @author Jackson
 * @since 1.4.0
 */
public class PollinatedSignItem extends SignItem {

    public PollinatedSignItem(Item.Properties properties, Block standingBlock, Block wallBlock) {
        super(properties, standingBlock, wallBlock);
    }

    @Override
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
        if (this.allowedIn(category))
            TabFiller.insert(new ItemStack(this), false, items, stack -> stack.getItem() instanceof SignItem);
    }
}
