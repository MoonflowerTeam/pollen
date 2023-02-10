package gg.moonflower.pollen.core.datagen;

import gg.moonflower.pollen.api.datagen.provider.loot_table.PollinatedLootGenerator;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;

import java.util.function.BiConsumer;

public class TestBlockLootGenerator implements PollinatedLootGenerator {

    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> registry) {
        register(registry, "test_diamond", LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.DIAMOND))));
    }

    private static void register(BiConsumer<ResourceLocation, LootTable.Builder> registry, String name, LootTable.Builder builder) {
        registry.accept(new ResourceLocation(Pollen.MOD_ID, name), builder);
    }
}
