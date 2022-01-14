package gg.moonflower.pollen.api.datagen.provider.tags;

import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.core.mixin.TagsProviderAccessor;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

/**
 * Wraps {@link TagsProvider} to enable optional tags while still extending {@link EntityTypeTagsProvider}.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class PollinatedEntityTypeTagsProvider extends EntityTypeTagsProvider {

    private final String domain;

    public PollinatedEntityTypeTagsProvider(DataGenerator dataGenerator, PollinatedModContainer container) {
        super(dataGenerator);
        this.domain = container.getId();
    }

    @Override
    protected PollinatedTagsProvider.PollinatedTagAppender<EntityType<?>> tag(Tag.Named<EntityType<?>> tag) {
        return new PollinatedTagsProvider.PollinatedTagAppender<>(this.getOrCreateRawBuilder(tag), this.registry, this.domain);
    }

    @Override
    protected Tag.Builder getOrCreateRawBuilder(Tag.Named<EntityType<?>> tag) {
        return ((TagsProviderAccessor) this).getBuilders().computeIfAbsent(tag.getName(), __ -> new Tag.Builder());
    }
}
