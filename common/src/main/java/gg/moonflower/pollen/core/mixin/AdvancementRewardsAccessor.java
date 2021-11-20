package gg.moonflower.pollen.core.mixin;

import net.minecraft.advancements.AdvancementRewards;
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
    void setExperience(int experience);

    @Accessor
    void setLoot(ResourceLocation[] loot);

    @Accessor
    void setRecipes(ResourceLocation[] recipes);
}
