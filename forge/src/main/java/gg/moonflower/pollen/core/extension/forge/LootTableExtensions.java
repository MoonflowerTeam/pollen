package gg.moonflower.pollen.core.extension.forge;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

public interface LootTableExtensions {

    List<LootPool> pollen_getPools();
}
