package gg.moonflower.pollen.core.mixin.forge;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(TagsProvider.class)
public class TagsProviderMixin<T> {

    @ModifyVariable(method = "m_176833_", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", shift = At.Shift.BEFORE), ordinal = 0)
    public List<Tag.BuilderEntry> modifyList(List<Tag.BuilderEntry> list) {
        list.clear();
        return list;
    }
}
