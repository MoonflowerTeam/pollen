package gg.moonflower.pollen.core.mixin.loot;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootPool.class)
public interface LootPoolAccessor {

    @Accessor
    LootPoolEntryContainer[] getEntries();

    @Accessor
    LootItemCondition[] getConditions();

    @Accessor
    LootItemFunction[] getFunctions();

    @Accessor
    NumberProvider getRolls();

    @Accessor
    NumberProvider getBonusRolls();
}
