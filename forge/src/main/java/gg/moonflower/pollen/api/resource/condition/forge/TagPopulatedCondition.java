package gg.moonflower.pollen.api.resource.condition.forge;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public abstract class TagPopulatedCondition<T> implements ICondition {

    private final ResourceKey<? extends Registry<T>> registry;
    private final TagKey<T> tag;

    public TagPopulatedCondition(ResourceKey<? extends Registry<T>> registry, ResourceLocation tag) {
        this.registry = registry;
        this.tag = TagKey.create(registry, tag);
    }

    @Override
    public boolean test(IContext context) {
        return Platform.getRegistryAccess().<Registry<T>>flatMap(access -> access.registry(this.registry)).stream().anyMatch(registry -> registry.isKnownTagName(this.tag));
    }

    @Override
    public String toString() {
        return this.registry.registry() + "_tag_populated(\"" + this.tag + "\")";
    }

    public record Serializer<T>(ResourceLocation name,
                                Function<ResourceLocation, TagPopulatedCondition<T>> factory) implements IConditionSerializer<TagPopulatedCondition<T>> {

        @Override
        public void write(JsonObject json, TagPopulatedCondition value) {
            json.addProperty("tag", value.tag.toString());
        }

        @Override
        public TagPopulatedCondition<T> read(JsonObject json) {
            return this.factory.apply(new ResourceLocation(GsonHelper.getAsString(json, "tag")));
        }

        @Override
        public ResourceLocation getID() {
            return this.name;
        }
    }
}
