package gg.moonflower.pollen.api.item;

import gg.moonflower.pollen.api.util.GroupUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * A modified item with the ability to "follow" any item in the creative menu instead of appearing at the bottom.
 *
 * @author Eltrutlikes
 * @author ebo2022
 */
public class FollowItem extends Item {
    private final Item followItem;

    public FollowItem(Properties properties, Item followItem) {
        super(properties);
        this.followItem = followItem;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        GroupUtil.fillItem(this.asItem(), this.followItem, group, items);
    }
}
