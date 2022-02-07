package gg.moonflower.pollen.core.mixin.loot;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootPoolEntryContainer.class)
public interface LootPoolEntryContainerAccessor {

    @Invoker("<init>")
    static LootPoolEntryContainer init(LootItemCondition[] lootItemConditions) {
        throw new AssertionError();
    }
}
