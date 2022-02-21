package gg.moonflower.pollen.core.mixin.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Advancement.Builder.class)
public interface AdvancementBuilderAccessor {
    @Accessor
    String[][] getRequirements();

    @Accessor
    void setRequirements(String[][] requirements);

    @Accessor
    AdvancementRewards getRewards();

    @Accessor
    void setRewards(AdvancementRewards rewards);
}
