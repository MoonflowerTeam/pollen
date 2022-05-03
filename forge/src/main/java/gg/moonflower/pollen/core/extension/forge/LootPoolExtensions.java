package gg.moonflower.pollen.core.extension.forge;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public interface LootPoolExtensions {

    List<LootPoolEntryContainer> pollen_getEntries();

    List<LootItemCondition> pollen_getConditions();
}
