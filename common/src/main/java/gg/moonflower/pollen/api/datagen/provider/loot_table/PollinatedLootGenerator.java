package gg.moonflower.pollen.api.datagen.provider.loot_table;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

/**
 * Generates loot tables through {@link PollinatedLootTableProvider}.
 *
 * @author Ocelot
 * @since 1.4.10
 */
public interface PollinatedLootGenerator {

    /**
     * Creates all the models just before saving all files.
     */
    void run(BiConsumer<ResourceLocation, LootTable.Builder> registry);
}
