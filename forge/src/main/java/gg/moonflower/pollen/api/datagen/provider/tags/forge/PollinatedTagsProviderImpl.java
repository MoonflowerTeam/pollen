//package gg.moonflower.pollen.api.datagen.provider.tags.forge;
//
//import com.google.gson.JsonObject;
//import gg.moonflower.pollen.api.datagen.provider.tags.PollinatedTagsProvider;
//import gg.moonflower.pollen.api.resource.condition.ConditionalTagEntry;
//import gg.moonflower.pollen.api.resource.condition.PollinatedResourceConditionProvider;
//import gg.moonflower.pollen.core.extensions.TagBuilderExtension;
//import net.minecraft.core.Registry;
//import net.minecraft.data.tags.TagsProvider;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.tags.TagBuilder;
//import net.minecraft.tags.TagEntry;
//import net.minecraft.tags.TagKey;
//
//import java.util.Objects;
//
//public class PollinatedTagsProviderImpl {
//    public static <T> PollinatedTagsProvider.PollinatedTagAppender<T> createTagAppender(TagBuilder builder, Registry<T> registry, String domain) {
//        return new PollinatedTagAppenderImpl<>(builder, registry, domain);
//    }
//
//    private static class PollinatedTagAppenderImpl<T> extends TagsProvider.TagAppender<T> implements PollinatedTagsProvider.PollinatedTagAppender<T> {
//
//        private final TagBuilder builder;
//        private final Registry<T> registry;
//
//        protected PollinatedTagAppenderImpl(TagBuilder builder, Registry<T> registry, String domain) {
//            super(builder, registry, domain);
//            this.builder = builder;
//            this.registry = registry;
//        }
//
//        @Override
//        public TagBuilder builder() {
//            return this.builder;
//        }
//
//        @Override
//        public Registry<T> registry() {
//            return this.registry;
//        }
//
//        @Override
//        public PollinatedTagAppenderImpl<T> add(T item) {
//            return (PollinatedTagAppenderImpl<T>) super.add(item);
//        }
//
//        @Override
//        public PollinatedTagAppenderImpl<T> addTag(TagKey<T> tag) {
//            return (PollinatedTagAppenderImpl<T>) super.addTag(tag);
//        }
//
//        // TODO override add and cast with access widener
//        @SafeVarargs
//        @Override
//        public final PollinatedTagAppenderImpl<T> pollenAdd(T... toAdd) {
//            return (PollinatedTagAppenderImpl<T>) super.add(toAdd);
//        }
//
//        @SafeVarargs
//        @Override
//        public final PollinatedTagAppenderImpl<T> addTag(TagKey<T>... values) {
//            for (TagKey<T> value : values)
//                this.addTag(value);
//            return this;
//        }
//
//        @Override
//        public PollinatedTagAppenderImpl<T> addOptional(ResourceLocation resourceLocation) {
//            return (PollinatedTagAppenderImpl<T>) super.addOptional(resourceLocation);
//        }
//
//        @Override
//        public PollinatedTagAppenderImpl<T> addOptionalTag(ResourceLocation resourceLocation) {
//            return (PollinatedTagAppenderImpl<T>) super.addOptionalTag(resourceLocation);
//        }
//
//        @Override
//        public PollinatedTagAppenderImpl<T> replace() {
//            return this.replace(true);
//        }
//
//        @Override
//        public PollinatedTagAppenderImpl<T> replace(boolean value) {
//            ((TagBuilderExtension) this.builder).pollen_replace(value);
//            return this;
//        }
//
//        private static JsonObject createConditionJson(PollinatedResourceConditionProvider[] conditions) {
//            JsonObject json = new JsonObject();
//            PollinatedResourceConditionProvider.write(json, conditions);
//            return json;
//        }
//
//        public PollinatedTagAppenderImpl<T> addConditional(T item, PollinatedResourceConditionProvider... conditions) {
//            if (conditions.length == 0)
//                return this.add(item);
//            this.builder.add(new ConditionalTagEntry(TagEntry.element(Objects.requireNonNull(this.registry.getKey(item))), createConditionJson(conditions)));
//            return this;
//        }
//
//        public PollinatedTagAppenderImpl<T> addConditionalTag(TagKey<T> tag, PollinatedResourceConditionProvider... conditions) {
//            if (conditions.length == 0)
//                return this.addTag(tag);
//            this.builder.add(new ConditionalTagEntry(TagEntry.tag(tag.location()), createConditionJson(conditions)));
//            return this;
//        }
//
//        public PollinatedTagAppenderImpl<T> addConditionalOptional(ResourceLocation item, PollinatedResourceConditionProvider... conditions) {
//            if (conditions.length == 0)
//                return this.addOptional(item);
//            this.builder.add(new ConditionalTagEntry(TagEntry.optionalElement(item), createConditionJson(conditions)));
//            return this;
//        }
//
//        public PollinatedTagAppenderImpl<T> addConditionalOptionalTag(ResourceLocation tag, PollinatedResourceConditionProvider... conditions) {
//            if (conditions.length == 0)
//                return this.addOptionalTag(tag);
//            this.builder.add(new ConditionalTagEntry(TagEntry.optionalTag(tag), createConditionJson(conditions));
//            return this;
//        }
//    }
//}
