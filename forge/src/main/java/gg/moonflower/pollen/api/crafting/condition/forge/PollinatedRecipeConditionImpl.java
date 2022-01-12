package gg.moonflower.pollen.api.crafting.condition.forge;

import gg.moonflower.pollen.api.crafting.condition.PollinatedRecipeConditionProvider;
import net.minecraft.resources.ResourceLocation;
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
        return wrap(new ItemExistsCondition(name.getNamespace(), name.getPath()));
    }

    public static PollinatedRecipeConditionProvider modLoaded(String modId) {
        return wrap(new ModLoadedCondition(modId));
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
