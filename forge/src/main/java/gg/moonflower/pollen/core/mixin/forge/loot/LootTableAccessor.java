package gg.moonflower.pollen.core.mixin.forge.loot;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootTable.class)
public interface LootTableAccessor {

    @Accessor(value = "field_186466_c", remap = false)
    List<LootPool> getPools();
}
