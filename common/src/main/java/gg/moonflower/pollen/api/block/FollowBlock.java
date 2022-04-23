package gg.moonflower.pollen.api.block;

import gg.moonflower.pollen.api.util.GroupUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * A modified block with the ability to "follow" any item in the creative menu instead of appearing at the bottom.
 *
 * @author Eltrutlikes
 * @author ebo2022
 */
public class FollowBlock extends Block {
    private final Item followItem;

    public FollowBlock(Properties properties, Item followItem) {
        super(properties);
        this.followItem = followItem;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        GroupUtil.fillItem(this.asItem(), followItem, group, items);
    }
}
