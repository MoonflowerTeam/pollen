package gg.moonflower.pollen.api.crafting.condition.forge;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.crafting.condition.PollinatedRecipeCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class PollinatedRecipeConditionWrapper implements ICondition {

    private final ResourceLocation id;
    private final boolean result;

    public PollinatedRecipeConditionWrapper(ResourceLocation id, boolean result) {
        this.id = id;
        this.result = result;
    }

    @Override
    public ResourceLocation getID() {
        return id;
    }

    @Override
    public boolean test() {
        return result;
    }

    public static class Serializer implements IConditionSerializer<PollinatedRecipeConditionWrapper> {

        private final ResourceLocation name;
        private final PollinatedRecipeCondition condition;

        public Serializer(ResourceLocation name, PollinatedRecipeCondition condition) {
            this.name = name;
            this.condition = condition;
        }

        @Override
        public void write(JsonObject json, PollinatedRecipeConditionWrapper value) {
        }

        @Override
        public PollinatedRecipeConditionWrapper read(JsonObject json) {
            return new PollinatedRecipeConditionWrapper(this.name, this.condition.test(json));
        }

        @Override
        public ResourceLocation getID() {
            return name;
        }

        public PollinatedRecipeCondition getCondition() {
            return condition;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Serializer that = (Serializer) o;
            return this.name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }
}
