package gg.moonflower.pollen.core.mixin.forge.loot;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootPool.class)
public interface LootPoolAccessor {

    @Invoker("<init>")
    static LootPool init(LootPoolEntryContainer[] lootPoolEntryContainers, LootItemCondition[] lootItemConditions, LootItemFunction[] lootItemFunctions, NumberProvider randomIntGenerator, NumberProvider randomValueBounds, String name) {
        return Platform.error();
    }
}
