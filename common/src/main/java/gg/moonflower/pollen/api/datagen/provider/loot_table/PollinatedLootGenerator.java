package gg.moonflower.pollen.api.datagen.provider.loot_table;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Generates loot tables through {@link PollinatedLootTableProvider}.
 *
 * @author Ocelot
 * @since 1.4.10
 */
public interface PollinatedLootGenerator extends Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {

    /**
     * Creates all the models just before saving all files.
     */
    @Override
    void accept(BiConsumer<ResourceLocation, LootTable.Builder> registry);
}
