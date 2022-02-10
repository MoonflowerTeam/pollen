package gg.moonflower.pollen.core.mixin.loot;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootPool.Builder.class)
public interface LootPoolBuilderAccessor {

    @Accessor
    List<LootPoolEntryContainer> getEntries();

    @Accessor
    List<LootItemCondition> getConditions();

    @Accessor
    List<LootItemFunction> getFunctions();

    @Accessor
    void setBonusRolls(NumberProvider bonusRolls);
}
