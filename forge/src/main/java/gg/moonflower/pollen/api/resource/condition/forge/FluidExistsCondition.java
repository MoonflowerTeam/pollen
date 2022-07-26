package gg.moonflower.pollen.api.resource.condition.forge;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class FluidExistsCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(Pollen.MOD_ID, "fluid_exists");
    private final ResourceLocation fluid;

    public FluidExistsCondition(ResourceLocation fluid) {
        this.fluid = fluid;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return ForgeRegistries.FLUIDS.containsKey(fluid);
    }

    @Override
    public String toString() {
        return "fluid_exists(\"" + this.fluid + "\")";
    }

    public static class Serializer implements IConditionSerializer<FluidExistsCondition> {

        public static final FluidExistsCondition.Serializer INSTANCE = new FluidExistsCondition.Serializer();

        @Override
        public void write(JsonObject json, FluidExistsCondition value) {
            json.addProperty("fluid", value.fluid.toString());
        }

        @Override
        public FluidExistsCondition read(JsonObject json) {
            return new FluidExistsCondition(new ResourceLocation(GsonHelper.getAsString(json, "fluid")));
        }

        @Override
        public ResourceLocation getID() {
            return FluidExistsCondition.NAME;
        }
    }
}
