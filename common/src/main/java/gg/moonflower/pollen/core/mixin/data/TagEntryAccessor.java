package gg.moonflower.pollen.core.mixin.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TagEntry.class)
public interface TagEntryAccessor {

    @Accessor
    ResourceLocation getId();

    @Accessor
    boolean isTag();

    @Accessor
    boolean isRequired();

    @Invoker
    ExtraCodecs.TagOrElementLocation invokeElementOrTag();
}
