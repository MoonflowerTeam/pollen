// TODO: Re-implement once forge fixes their shit
//package gg.moonflower.pollen.api.datagen.provider.tags;
//
//import com.google.gson.JsonObject;
//import dev.architectury.injectables.annotations.ExpectPlatform;
//import gg.moonflower.pollen.api.datagen.provider.ConditionalDataProvider;
//import gg.moonflower.pollen.api.platform.Platform;
//import gg.moonflower.pollen.api.resource.condition.ConditionalTagEntry;
//import gg.moonflower.pollen.api.resource.condition.PollinatedResourceConditionProvider;
//import gg.moonflower.pollen.api.util.PollinatedModContainer;
//import gg.moonflower.pollen.core.extensions.TagBuilderExtension;
//import gg.moonflower.pollen.core.mixin.data.TagsProviderAccessor;
//import net.minecraft.core.Registry;
//import net.minecraft.data.DataGenerator;
//import net.minecraft.data.tags.TagsProvider;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.tags.TagBuilder;
//import net.minecraft.tags.TagEntry;
//import net.minecraft.tags.TagKey;
//
//import java.util.*;
//
///**
// * Wraps {@link TagsProvider} to enable optional tags.
// *
// * @author Ocelot
// * @since 1.0.0
// */
//public abstract class PollinatedTagsProvider<T> extends TagsProvider<T> implements ConditionalDataProvider {
//
//    private final Map<ResourceLocation, List<PollinatedResourceConditionProvider>> providers;
//    private final String domain;
//
//    public PollinatedTagsProvider(DataGenerator generator, PollinatedModContainer container, Registry<T> registry) {
//        super(generator, registry);
//        this.providers = new HashMap<>();
//        this.domain = container.getId();
//    }
//
//    /**
//     * Adds a condition to the specified tag.
//     *
//     * @param tag       The tag to add conditions to
//     * @param providers The conditions to add
//     */
//    public void addConditions(TagKey<T> tag, PollinatedResourceConditionProvider... providers) {
//        this.addConditions(tag.location(), providers);
//    }
//
//    @Override
//    public void addConditions(ResourceLocation id, PollinatedResourceConditionProvider... providers) {
//        if (providers.length == 0)
//            return;
//        this.providers.computeIfAbsent(id, __ -> new ArrayList<>()).addAll(Arrays.asList(providers));
//    }
//
//    @Override
//    public void injectConditions(ResourceLocation id, JsonObject json) {
//        if (this.providers.containsKey(id))
//            PollinatedResourceConditionProvider.write(json, this.providers.get(id).toArray(new PollinatedResourceConditionProvider[0]));
//    }
//
//    protected PollinatedTagsProvider.PollinatedTagAppender<T> pollenTag(TagKey<T> tag) { // thanks forge for adding an extra constructor parameter
//        return PollinatedTagsProvider.createTagAppender(this.getOrCreateRawBuilder(tag), this.registry, this.domain);
//    }
//
//    @Override
//    protected TagBuilder getOrCreateRawBuilder(TagKey<T> tag) {
//        return ((TagsProviderAccessor) this).getBuilders().computeIfAbsent(tag.location(), __ -> new TagBuilder());
//    }
//
//    @ExpectPlatform
//    public static <T> PollinatedTagAppender<T> createTagAppender(TagBuilder builder, Registry<T> registry, String domain) {
//        return Platform.error();
//    }
//
//    /**
//     * An extended version of {@link TagAppender} enabling the usage of optional entries.
//     *
//     * @param <T>
//     * @author Ocelot
//     * @since 1.0.0
//     */
//    @SuppressWarnings("VariableArgumentMethod")
//    public interface PollinatedTagAppender<T>  {
//
//        TagBuilder builder();
//
//        Registry<T> registry();
//
//        PollinatedTagAppender<T> add(T item);
//
//        PollinatedTagAppender<T> addTag(TagKey<T> tag);
//
//        PollinatedTagAppender<T> pollenAdd(T... toAdd);
//
//        PollinatedTagAppender<T> addTag(TagKey<T>... values);
//
//        PollinatedTagAppender<T> addOptional(ResourceLocation resourceLocation);
//
//        PollinatedTagAppender<T> addOptionalTag(ResourceLocation resourceLocation);
//
//        default PollinatedTagAppender<T> replace() {
//            return this.replace(true);
//        }
//
//        default PollinatedTagAppender<T> replace(boolean value) {
//            ((TagBuilderExtension) this.builder()).pollen_replace(value);
//            return this;
//        }
//
//        private static JsonObject createConditionJson(PollinatedResourceConditionProvider[] conditions) {
//            JsonObject json = new JsonObject();
//            PollinatedResourceConditionProvider.write(json, conditions);
//            return json;
//        }
//
//        default PollinatedTagAppender<T> addConditional(T item, PollinatedResourceConditionProvider... conditions) {
//            if (conditions.length == 0)
//                return this.add(item);
//            this.builder().add(new ConditionalTagEntry(TagEntry.element(Objects.requireNonNull(this.registry().getKey(item))), createConditionJson(conditions)));
//            return this;
//        }
//
//        default PollinatedTagAppender<T> addConditionalTag(TagKey<T> tag, PollinatedResourceConditionProvider... conditions) {
//            if (conditions.length == 0)
//                return this.addTag(tag);
//            this.builder().add(new ConditionalTagEntry(TagEntry.tag(tag.location()), createConditionJson(conditions)));
//            return this;
//        }
//
//        default PollinatedTagAppender<T> addConditionalOptional(ResourceLocation item, PollinatedResourceConditionProvider... conditions) {
//            if (conditions.length == 0)
//                return this.addOptional(item);
//            this.builder().add(new ConditionalTagEntry(TagEntry.optionalElement(item), createConditionJson(conditions)));
//            return this;
//        }
//
//        default PollinatedTagAppender<T> addConditionalOptionalTag(ResourceLocation tag, PollinatedResourceConditionProvider... conditions) {
//            if (conditions.length == 0)
//                return this.addOptionalTag(tag);
//            this.builder().add(new ConditionalTagEntry(TagEntry.optionalTag(tag), createConditionJson(conditions)));
//            return this;
//        }
//    }
//}
