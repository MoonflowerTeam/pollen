package gg.moonflower.pollen.api.resource.condition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.api.registry.ResourceConditionRegistry;
import gg.moonflower.pollen.core.mixin.data.TagEntryAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@ApiStatus.Internal
public class ConditionalTagEntry extends TagEntry {

    public static final Codec<TagEntry> FULL_CODEC = Codec.pair(TagEntry.CODEC, new Codec<JsonObject>() {
        @Override
        public <T> DataResult<Pair<JsonObject, T>> decode(DynamicOps<T> ops, T input) {
            JsonElement element = ops.convertMap(JsonOps.INSTANCE, input);
            if (!element.isJsonObject())
                return DataResult.error("Json is not an object");

            return DataResult.success(Pair.of(element.getAsJsonObject(), input));
        }

        @Override
        public <T> DataResult<T> encode(JsonObject input, DynamicOps<T> ops, T prefix) {
            return DataResult.success(JsonOps.INSTANCE.convertTo(ops, input));
        }
    }.optionalFieldOf(ResourceConditionRegistry.getConditionsKey()).codec()).xmap(pair -> pair.getSecond().isEmpty() ? pair.getFirst() : new ConditionalTagEntry(pair.getFirst(), pair.getSecond().get()), entry -> entry instanceof ConditionalTagEntry c ? Pair.of(c.entry, Optional.of(c.json)) : Pair.of(entry, Optional.empty()));

    public static final Codec<TagFile> TAG_FILE_CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
                ConditionalTagEntry.FULL_CODEC.listOf().fieldOf("values").forGetter(TagFile::entries),
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(TagFile::replace)
            )
            .apply(instance, TagFile::new)
    );

    private final TagEntry entry;
    private final JsonObject json;

    public ConditionalTagEntry(TagEntry entry, JsonObject json) {
        super(((TagEntryAccessor) entry).getId(), ((TagEntryAccessor) entry).isTag(), ((TagEntryAccessor) entry).isRequired());
        this.entry = entry;
        this.json = json;
    }

    @Override
    public <T> boolean build(TagEntry.Lookup<T> lookup, Consumer<T> consumer) {
        return !ResourceConditionRegistry.test(this.json) || this.entry.build(lookup, consumer);
    }

    @Override
    public boolean verifyIfPresent(Predicate<ResourceLocation> predicate, Predicate<ResourceLocation> predicate2) {
        return !ResourceConditionRegistry.test(this.json) || this.entry.verifyIfPresent(predicate, predicate2);
    }
}
