package gg.moonflower.pollen.api.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A simple registry to set how blocks interact with Fire.
 *
 * @author Eltrutlikes
 * @author ebo2022
 */
public class FlammableRegistry {

    /**
     * Registers flammability behavior for the specified block.
     *
     * @param block         The block to register flammability behavior for
     * @param encouragement How quickly the block will catch fire
     * @param flammability  How quickly the block burns away when on fire
     */
    public static void register(Block block, int encouragement, int flammability) {
        FireBlock fireBlock = (FireBlock) Blocks.FIRE;
        fireBlock.setFlammable(block, encouragement, flammability);
    }

    /**
     * Defaulted values for flammable blocks.
     */
    public static class FlammableChance {
        public static final Pair<Integer, Integer> WOOD = Pair.of(5, 5);
        public static final Pair<Integer, Integer> PLANKS = Pair.of(5, 20);
        public static final Pair<Integer, Integer> BOOKSHELF = Pair.of(30, 20);
        public static final Pair<Integer, Integer> LEAVES = Pair.of(30, 60);
        public static final Pair<Integer, Integer> WOOL = Pair.of(30, 60);
        public static final Pair<Integer, Integer> CARPET = Pair.of(60, 20);
        public static final Pair<Integer, Integer> FLOWER = Pair.of(60, 100);
    }
}