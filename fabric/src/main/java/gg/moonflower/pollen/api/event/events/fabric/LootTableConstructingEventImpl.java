package gg.moonflower.pollen.api.event.events.fabric;

import gg.moonflower.pollen.core.mixin.fabric.loot.LootPoolAccessor;
import gg.moonflower.pollen.core.mixin.fabric.loot.LootTableAccessor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Arrays;
import java.util.List;

public class LootTableConstructingEventImpl {

    public static List<LootPoolEntryContainer> getEntries(LootPool lootPool) {
        return Arrays.asList(((LootPoolAccessor) lootPool).getEntries());
    }

    public static List<LootItemCondition> getConditions(LootPool lootPool) {
        return Arrays.asList(((LootPoolAccessor) lootPool).getConditions());
    }

    public static List<LootPool> getPools(LootTable lootTable) {
        return Arrays.asList(((LootTableAccessor) lootTable).getPools());
    }
}
