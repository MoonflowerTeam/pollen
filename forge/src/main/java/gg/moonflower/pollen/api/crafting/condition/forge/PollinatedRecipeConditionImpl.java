package gg.moonflower.pollen.api.crafting.condition.forge;

import gg.moonflower.pollen.api.crafting.condition.PollinatedRecipeConditionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.conditions.*;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;

@ApiStatus.Internal
public class PollinatedRecipeConditionImpl {

    private static final PollinatedRecipeConditionProvider FALSE = wrap(FalseCondition.INSTANCE);
    private static final PollinatedRecipeConditionProvider TRUE = wrap(TrueCondition.INSTANCE);

    public static PollinatedRecipeConditionProvider and(PollinatedRecipeConditionProvider... values) {
        return wrap(new AndCondition(Arrays.stream(values).map(ConditionWrapper::new).toArray(ICondition[]::new)));
    }

    public static PollinatedRecipeConditionProvider FALSE() {
        return FALSE;
    }

    public static PollinatedRecipeConditionProvider TRUE() {
        return TRUE;
    }

    public static PollinatedRecipeConditionProvider not(PollinatedRecipeConditionProvider value) {
        return wrap(new NotCondition(new ConditionWrapper(value)));
    }

    public static PollinatedRecipeConditionProvider or(PollinatedRecipeConditionProvider... values) {
        return wrap(new OrCondition(Arrays.stream(values).map(ConditionWrapper::new).toArray(ICondition[]::new)));
    }

    public static PollinatedRecipeConditionProvider itemExists(ResourceLocation name) {
        return wrap(new ItemExistsCondition(name));
    }

    public static PollinatedRecipeConditionProvider blockExists(ResourceLocation name) {
        return wrap(new BlockExistsCondition(name));
    }

    public static PollinatedRecipeConditionProvider fluidExists(ResourceLocation name) {
        return wrap(new FluidExistsCondition(name));
    }

    public static PollinatedRecipeConditionProvider itemTagPopulated(Tag.Named<Item> tag) {
        return wrap(new ItemTagPopulatedCondition(tag.getName()));
    }

    public static PollinatedRecipeConditionProvider blockTagPopulated(Tag.Named<Block> tag) {
        return wrap(new BlockTagPopulatedCondition(tag.getName()));
    }

    public static PollinatedRecipeConditionProvider fluidTagPopulated(Tag.Named<Fluid> tag) {
        return wrap(new FluidTagPopulatedCondition(tag.getName()));
    }

    public static PollinatedRecipeConditionProvider allModsLoaded(String... modIds) {
        return modIds.length == 1 ? wrap(new ModLoadedCondition(modIds[0])) : wrap(new AndCondition(Arrays.stream(modIds).map(ModLoadedCondition::new).toArray(ICondition[]::new)));
    }

    public static PollinatedRecipeConditionProvider anyModsLoaded(String... modIds) {
        return modIds.length == 1 ? wrap(new ModLoadedCondition(modIds[0])) : wrap(new OrCondition(Arrays.stream(modIds).map(ModLoadedCondition::new).toArray(ICondition[]::new)));
    }

    private static PollinatedRecipeConditionProvider wrap(ICondition condition) {
        return new ForgeRecipeConditionProvider(condition);
    }

    private static class ConditionWrapper implements ICondition {

        private final PollinatedRecipeConditionProvider provider;

        private ConditionWrapper(PollinatedRecipeConditionProvider provider) {
            this.provider = provider;
        }

        @Override
        public ResourceLocation getID() {
            return this.provider.getName();
        }

        @Override
        public boolean test() {
            return false;
        }
    }
}
