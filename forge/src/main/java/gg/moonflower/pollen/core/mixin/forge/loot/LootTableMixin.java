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

    @Shadow(remap = false)
    @Final
    private List<LootPool> f_79109_;

    @Override
    public List<LootPool> pollen_getPools() {
        return this.f_79109_;
    }
}
