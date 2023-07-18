package gg.moonflower.pollen.api.registry.content.v1;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Registers flammability to fire blocks.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface FlammabilityRegistry {

    /**
     * Registers flammability for the specified block.
     *
     * @param block         The block to light on fire
     * @param encouragement The probability of the block lighting on fire. Used in <code>(encouragement + 40 + level.getDifficulty().getId() * 7) / (fireAge + 30)</code>
     * @param flammability  The probability of the block burning up on the fire tick. 0 is 0% and 300 is 100%
     */
    static void register(Block block, int encouragement, int flammability) {
        register(Blocks.FIRE, block, encouragement, flammability);
    }

    /**
     * Registers flammability for the specified fire block.
     *
     * @param fireBlock     The block to use as fire. If unsure, use {@link FlammabilityRegistry#register(Block, int, int)} instead
     * @param block         The block to light on fire
     * @param encouragement The probability of the block lighting on fire. Used in <code>(encouragement + 40 + level.getDifficulty().getId() * 7) / (fireAge + 30)</code>
     * @param flammability  The probability of the block burning up on the fire tick. 0 is 0% and 300 is 100%
     */
    @ExpectPlatform
    static void register(Block fireBlock, Block block, int encouragement, int flammability) {
        Pollen.expect();
    }
}
