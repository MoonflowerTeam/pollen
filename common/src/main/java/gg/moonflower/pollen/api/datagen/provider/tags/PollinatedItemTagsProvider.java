package gg.moonflower.pollen.api.datagen.provider.tags;

import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.core.mixin.TagsProviderAccessor;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

/**
 * Wraps {@link TagsProvider} to enable optional tags while still extending {@link ItemTagsProvider}.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class PollinatedItemTagsProvider extends ItemTagsProvider {

    private final String domain;

    public PollinatedItemTagsProvider(DataGenerator dataGenerator, PollinatedModContainer container, BlockTagsProvider blockTagsProvider) {
        super(dataGenerator, blockTagsProvider);
        this.domain = container.getId();
    }

    @Override
    protected PollinatedTagsProvider.PollinatedTagAppender<Item> tag(Tag.Named<Item> tag) {
        return new PollinatedTagsProvider.PollinatedTagAppender<>(this.getOrCreateRawBuilder(tag), this.registry, this.domain);
    }

    @Override
    protected Tag.Builder getOrCreateRawBuilder(Tag.Named<Item> tag) {
        return ((TagsProviderAccessor) this).getBuilders().computeIfAbsent(tag.getName(), __ -> new Tag.Builder());
    }
}
