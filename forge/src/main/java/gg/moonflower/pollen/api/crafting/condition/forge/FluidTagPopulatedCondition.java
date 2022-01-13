package gg.moonflower.pollen.api.crafting.condition.forge;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class FluidTagPopulatedCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(Pollen.MOD_ID, "fluid_tag_populated");
    private final ResourceLocation tag;

    public FluidTagPopulatedCondition(ResourceLocation tag) {
        this.tag = tag;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test() {
        return SerializationTags.getInstance().getOrEmpty(Registry.FLUID_REGISTRY).getAvailableTags().contains(this.tag);
    }

    @Override
    public String toString() {
        return "fluid_tag_populated(\"" + this.tag + "\")";
    }

    public static class Serializer implements IConditionSerializer<FluidTagPopulatedCondition> {

        public static final FluidTagPopulatedCondition.Serializer INSTANCE = new FluidTagPopulatedCondition.Serializer();

        @Override
        public void write(JsonObject json, FluidTagPopulatedCondition value) {
            json.addProperty("tag", value.tag.toString());
        }

        @Override
        public FluidTagPopulatedCondition read(JsonObject json) {
            return new FluidTagPopulatedCondition(new ResourceLocation(GsonHelper.getAsString(json, "tag")));
        }

        @Override
        public ResourceLocation getID() {
            return FluidTagPopulatedCondition.NAME;
        }
    }
}
