package gg.moonflower.pollen.core.mixin.forge.loot;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(LootPool.class)
public interface LootPoolAccessor {

    @Invoker("<init>")
    static LootPool init(LootPoolEntryContainer[] lootPoolEntryContainers, LootItemCondition[] lootItemConditions, LootItemFunction[] lootItemFunctions, RandomIntGenerator randomIntGenerator, RandomValueBounds randomValueBounds, String name) {
        return Platform.error();
    }

    @Accessor(value = "field_186453_a", remap = false)
    List<LootPoolEntryContainer> getEntries();

    @Accessor(value = "field_186454_b", remap = false)
    List<LootItemCondition> getConditions();
}
