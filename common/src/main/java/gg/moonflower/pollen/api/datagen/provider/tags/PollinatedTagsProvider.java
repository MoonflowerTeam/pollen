package gg.moonflower.pollen.api.datagen.provider.tags;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.datagen.provider.ConditionalDataProvider;
import gg.moonflower.pollen.api.resource.condition.ConditionalTagEntry;
import gg.moonflower.pollen.api.resource.condition.PollinatedResourceConditionProvider;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.core.extensions.TagBuilderExtension;
import gg.moonflower.pollen.core.mixin.data.TagsProviderAccessor;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;

import java.util.*;

/**
 * Wraps {@link TagsProvider} to enable optional tags.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class PollinatedTagsProvider<T> extends TagsProvider<T> implements ConditionalDataProvider {

    private final Map<ResourceLocation, List<PollinatedResourceConditionProvider>> providers;
    private final String domain;

    public PollinatedTagsProvider(DataGenerator generator, PollinatedModContainer container, Registry<T> registry) {
        super(generator, registry);
        this.providers = new HashMap<>();
        this.domain = container.getId();
    }

    /**
     * Adds a condition to the specified tag.
     *
     * @param tag       The tag to add conditions to
     * @param providers The conditions to add
     */
    public void addConditions(Tag.Named<T> tag, PollinatedResourceConditionProvider... providers) {
        this.addConditions(tag.getName(), providers);
    }

    @Override
    public void addConditions(ResourceLocation id, PollinatedResourceConditionProvider... providers) {
        if (providers.length == 0)
            return;
        this.providers.computeIfAbsent(id, __ -> new ArrayList<>()).addAll(Arrays.asList(providers));
    }

    @Override
    public void injectConditions(ResourceLocation id, JsonObject json) {
        if (this.providers.containsKey(id))
            PollinatedResourceConditionProvider.write(json, this.providers.get(id).toArray(new PollinatedResourceConditionProvider[0]));
    }

    @Override
    protected PollinatedTagsProvider.PollinatedTagAppender<T> tag(Tag.Named<T> tag) {
        return new PollinatedTagsProvider.PollinatedTagAppender<>(this.getOrCreateRawBuilder(tag), this.registry, this.domain);
    }

    @Override
    protected Tag.Builder getOrCreateRawBuilder(Tag.Named<T> tag) {
        return ((TagsProviderAccessor) this).getBuilders().computeIfAbsent(tag.getName(), __ -> new Tag.Builder());
    }

    /**
     * An extended version of {@link TagAppender} enabling the usage of optional entries.
     *
     * @param <T>
     * @author Ocelot
     * @since 1.0.0
     */
    public static class PollinatedTagAppender<T> extends TagsProvider.TagAppender<T> {

        private final Tag.Builder builder;
        private final Registry<T> registry;
        private final String source;

        protected PollinatedTagAppender(Tag.Builder builder, Registry<T> registry, String source) {
            super(builder, registry, source);
            this.builder = builder;
            this.registry = registry;
            this.source = source;
        }

        @Override
        public PollinatedTagAppender<T> add(T item) {
            return (PollinatedTagAppender<T>) super.add(item);
        }

        @Override
        public PollinatedTagAppender<T> addTag(Tag.Named<T> tag) {
            return (PollinatedTagAppender<T>) super.addTag(tag);
        }

        // TODO override add and cast with access widener
        @SafeVarargs
        public final PollinatedTagAppender<T> pollenAdd(T... toAdd) {
            return (PollinatedTagAppender<T>) super.add(toAdd);
        }

        @SafeVarargs
        public final PollinatedTagAppender<T> addTag(Tag.Named<T>... values) {
            for (Tag.Named<T> value : values)
                this.addTag(value);
            return this;
        }

        @SafeVarargs
        public final PollinatedTagAppender<T> add(ResourceKey<T>... keys) {
            for (ResourceKey<T> key : keys)
                this.builder.addElement(key.location(), this.source);
            return this;
        }

        @Override
        public PollinatedTagAppender<T> addOptional(ResourceLocation resourceLocation) {
            return (PollinatedTagAppender<T>) super.addOptional(resourceLocation);
        }

        @Override
        public PollinatedTagAppender<T> addOptionalTag(ResourceLocation resourceLocation) {
            return (PollinatedTagAppender<T>) super.addOptionalTag(resourceLocation);
        }

        public PollinatedTagAppender<T> replace() {
            return this.replace(true);
        }

        public PollinatedTagAppender<T> replace(boolean value) {
            ((TagBuilderExtension) this.builder).pollen_replace(value);
            return this;
        }

        private static JsonObject createConditionJson(PollinatedResourceConditionProvider[] conditions) {
            JsonObject json = new JsonObject();
            PollinatedResourceConditionProvider.write(json, conditions);
            return json;
        }

        public PollinatedTagAppender<T> addConditional(T item, PollinatedResourceConditionProvider... conditions) {
            if (conditions.length == 0)
                return this.add(item);
            this.builder.add(new ConditionalTagEntry(new Tag.ElementEntry(this.registry.getKey(item)), createConditionJson(conditions)), this.source);
            return this;
        }

        public PollinatedTagAppender<T> addConditionalTag(Tag.Named<T> tag, PollinatedResourceConditionProvider... conditions) {
            if (conditions.length == 0)
                return this.addTag(tag);
            this.builder.add(new ConditionalTagEntry(new Tag.TagEntry(tag.getName()), createConditionJson(conditions)), this.source);
            return this;
        }

        public PollinatedTagAppender<T> addConditionalOptional(ResourceLocation item, PollinatedResourceConditionProvider... conditions) {
            if (conditions.length == 0)
                return this.addOptional(item);
            this.builder.add(new ConditionalTagEntry(new Tag.OptionalElementEntry(item), createConditionJson(conditions)), this.source);
            return this;
        }

        public PollinatedTagAppender<T> addConditionalOptionalTag(ResourceLocation tag, PollinatedResourceConditionProvider... conditions) {
            if (conditions.length == 0)
                return this.addOptionalTag(tag);
            this.builder.add(new ConditionalTagEntry(new Tag.OptionalTagEntry(tag), createConditionJson(conditions)), this.source);
            return this;
        }
    }
}
