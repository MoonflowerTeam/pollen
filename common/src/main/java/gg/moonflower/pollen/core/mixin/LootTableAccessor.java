package gg.moonflower.pollen.core.mixin;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootTable.class)
public interface LootTableAccessor {

    @Accessor
    LootPool[] getPools();

    @Accessor
    LootItemFunction[] getFunctions();

    @Invoker("<init>")
    static LootTable init(LootContextParamSet lootContextParamSet, LootPool[] lootPools, LootItemFunction[] lootItemFunctions) {
        throw new AssertionError();
    }
}
