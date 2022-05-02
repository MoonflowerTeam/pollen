package gg.moonflower.pollen.core.mixin.forge.loot;

import gg.moonflower.pollen.core.extension.forge.LootTableExtensions;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LootTable.class)
public class LootTableMixin implements LootTableExtensions {

    @Shadow
    @Final
    private List<LootPool> field_186466_c;

    @Override
    public List<LootPool> pollen_getPools() {
        return this.field_186466_c;
    }
}
