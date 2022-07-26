package gg.moonflower.pollen.api.resource.condition.forge;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class PollinatedRecipeConditionWrapper implements ICondition {

    private final ResourceLocation name;
    private final String stringName;
    private final boolean result;

    public PollinatedRecipeConditionWrapper(ResourceLocation name, String stringName, boolean result) {
        this.name = name;
        this.stringName = stringName;
        this.result = result;
    }

    @Override
    public ResourceLocation getID() {
        return name;
    }

    @Override
    public boolean test(IContext context) {
        return result;
    }

    @Override
    public String toString() {
        return stringName;
    }

    public static class Serializer implements IConditionSerializer<PollinatedRecipeConditionWrapper> {

        private final ResourceLocation name;
        private final PollinatedResourceCondition condition;

        public Serializer(ResourceLocation name, PollinatedResourceCondition condition) {
            this.name = name;
            this.condition = condition;
        }

        @Override
        public void write(JsonObject json, PollinatedRecipeConditionWrapper value) {
        }

        @Override
        public PollinatedRecipeConditionWrapper read(JsonObject json) {
            return new PollinatedRecipeConditionWrapper(this.name, this.condition.toString(), this.condition.test(json));
        }

        @Override
        public ResourceLocation getID() {
            return name;
        }

        public PollinatedResourceCondition getCondition() {
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
