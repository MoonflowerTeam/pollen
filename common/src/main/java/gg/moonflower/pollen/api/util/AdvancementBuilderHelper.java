package gg.moonflower.pollen.api.util;

import gg.moonflower.pollen.core.mixin.data.AdvancementRewardsBuilderAccessor;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourceLocation;

/**
 * Assists in building advancements by exposing methods compiled out by ProGuard.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class AdvancementBuilderHelper {

    private AdvancementBuilderHelper() {
    }

    /**
     * Creates a new advancement reward with only a single loot table.
     *
     * @param lootTable The loot table to reward the player
     * @return A new builder
     */
    public static AdvancementRewards.Builder loot(ResourceLocation lootTable) {
        return addLoot(new AdvancementRewards.Builder(), lootTable);
    }

    /**
     * Adds a loot table to the specified advancement reward.
     *
     * @param builder   The builder to add the reward to
     * @param lootTable The loot table to reward the player
     */
    public static AdvancementRewards.Builder addLoot(AdvancementRewards.Builder builder, ResourceLocation lootTable) {
        ((AdvancementRewardsBuilderAccessor) builder).getLoot().add(lootTable);
        return builder;
    }
}
