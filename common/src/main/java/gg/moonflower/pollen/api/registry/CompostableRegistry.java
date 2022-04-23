package gg.moonflower.pollen.api.registry;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;


/**
 * A simple registry to add Composter behavior to items.
 *
 * @author Eltrutlikes
 * @author ebo2022
 */
public class CompostableRegistry {

    /**
     * Registers composting behavior for the specified item.
     *
     * @param item              The item to register compost behavior for
     * @param compostableChance The chance (as a {@link Float}) that the item will fill a composter
     */
    public static void register(ItemLike item, float compostableChance) {
        ComposterBlock.add(compostableChance, item);
    }

    /**
     * Defaulted chances for compostable blocks and items.
     */
    public static class CompostableChance {
        public static final float SEEDS = 0.3F;
        public static final float PLANTS = 0.65F;
        public static final float BAKED_GOODS = 0.85F;
        public static final float PIES = 1.0F;
    }
}
