package gg.moonflower.pollen.api.datagen.provider.tags;

import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.core.mixin.TagsProviderAccessor;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.material.Fluid;

/**
 * Wraps {@link TagsProvider} to enable optional tags while still extending {@link FluidTagsProvider}.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class PollinatedFluidTagsProvider extends FluidTagsProvider {

    private final String domain;

    public PollinatedFluidTagsProvider(DataGenerator dataGenerator, PollinatedModContainer container) {
        super(dataGenerator);
        this.domain = container.getId();
    }

    @Override
    protected PollinatedTagsProvider.PollinatedTagAppender<Fluid> tag(Tag.Named<Fluid> tag) {
        return new PollinatedTagsProvider.PollinatedTagAppender<>(this.getOrCreateRawBuilder(tag), this.registry, this.domain);
    }

    @Override
    protected Tag.Builder getOrCreateRawBuilder(Tag.Named<Fluid> tag) {
        return ((TagsProviderAccessor) this).getBuilders().computeIfAbsent(tag.getName(), __ -> new Tag.Builder());
    }
}
