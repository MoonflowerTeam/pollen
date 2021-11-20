package gg.moonflower.pollen.core.mixin;

import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(AdvancementRewards.Builder.class)
public interface AdvancementRewardsBuilderAccessor {
    @Accessor
    List<ResourceLocation> getLoot();
}
