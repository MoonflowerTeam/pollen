package gg.moonflower.pollen.api.event.events.forge;

import gg.moonflower.pollen.core.mixin.forge.loot.LootTableAccessor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class LootTableConstructingEventImpl {

    public static List<LootPool> getPools(LootTable lootTable) {
        return ((LootTableAccessor) lootTable).getPools();
    }
}
