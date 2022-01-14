package gg.moonflower.pollen.core.mixin;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(TagsProvider.class)
public interface TagsProviderAccessor {

    @Accessor
    Map<ResourceLocation, Tag.Builder> getBuilders();
}
