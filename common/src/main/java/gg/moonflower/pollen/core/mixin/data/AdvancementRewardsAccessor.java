package gg.moonflower.pollen.core.mixin.data;

import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementRewards.class)
public interface AdvancementRewardsAccessor {
    @Accessor
    int getExperience();

    @Accessor
    ResourceLocation[] getLoot();

    @Accessor
    ResourceLocation[] getRecipes();

    @Accessor
    CommandFunction.CacheableFunction getFunction();
}
