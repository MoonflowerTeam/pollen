package gg.moonflower.pollen.core.mixin.forge.loot;

import gg.moonflower.pollen.core.extension.forge.LootPoolExtensions;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LootPool.class)
public class LootPoolMixin implements LootPoolExtensions {

    @Shadow
    @Final
    private List<LootPoolEntryContainer> field_186453_a;

    @Shadow
    @Final
    private List<LootItemCondition> field_186454_b;

    @Override
    public List<LootPoolEntryContainer> pollen_getEntries() {
        return this.field_186453_a;
    }

    @Override
    public List<LootItemCondition> pollen_getConditions() {
        return this.field_186454_b;
    }
}
