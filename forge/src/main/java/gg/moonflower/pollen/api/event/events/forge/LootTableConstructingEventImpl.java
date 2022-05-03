package gg.moonflower.pollen.api.event.events.forge;

import gg.moonflower.pollen.core.extension.forge.LootPoolExtensions;
import gg.moonflower.pollen.core.extension.forge.LootTableExtensions;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class LootTableConstructingEventImpl {

    public static List<LootPoolEntryContainer> getEntries(LootPool lootPool) {
        return ((LootPoolExtensions) lootPool).pollen_getEntries();
    }

    public static List<LootItemCondition> getConditions(LootPool lootPool) {
        return ((LootPoolExtensions) lootPool).pollen_getConditions();
    }

    public static List<LootPool> getPools(LootTable lootTable) {
        return ((LootTableExtensions) lootTable).pollen_getPools();
    }
}
