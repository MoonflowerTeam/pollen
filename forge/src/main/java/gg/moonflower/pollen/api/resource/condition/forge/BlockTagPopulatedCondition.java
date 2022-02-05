package gg.moonflower.pollen.api.resource.condition.forge;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.core.Pollen;
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
public class BlockTagPopulatedCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation(Pollen.MOD_ID, "block_tag_populated");
    private final ResourceLocation tag;

    public BlockTagPopulatedCondition(ResourceLocation tag) {
        this.tag = tag;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test() {
        return SerializationTags.getInstance().getBlocks().getAvailableTags().contains(this.tag);
    }

    @Override
    public String toString() {
        return "block_tag_populated(\"" + this.tag + "\")";
    }

    public static class Serializer implements IConditionSerializer<BlockTagPopulatedCondition> {

        public static final BlockTagPopulatedCondition.Serializer INSTANCE = new BlockTagPopulatedCondition.Serializer();

        @Override
        public void write(JsonObject json, BlockTagPopulatedCondition value) {
            json.addProperty("tag", value.tag.toString());
        }

        @Override
        public BlockTagPopulatedCondition read(JsonObject json) {
            return new BlockTagPopulatedCondition(new ResourceLocation(GsonHelper.getAsString(json, "tag")));
        }

        @Override
        public ResourceLocation getID() {
            return BlockTagPopulatedCondition.NAME;
        }
    }
}
