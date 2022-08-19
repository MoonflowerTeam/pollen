package gg.moonflower.pollen.core.mixin.loot;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootPool.class)
public interface LootPoolAccessor {

    @Accessor
    LootItemFunction[] getFunctions();

    @Accessor("rolls")
    RandomIntGenerator getPoolRolls();

    @Accessor
    RandomValueBounds getBonusRolls();
}
