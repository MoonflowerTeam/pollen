package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.core.extensions.TagBuilderExtension;
import net.minecraft.tags.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Tag.Builder.class)
public class TagBuilderMixin implements TagBuilderExtension {

    @Unique
    private boolean replace;

    @Override
    public void pollen_replace(boolean value) {
        this.replace = value;
    }

    @ModifyArg(method = "serializeToJson", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;addProperty(Ljava/lang/String;Ljava/lang/Boolean;)V", remap = false))
    public Boolean modifyReplace(Boolean value) {
        return this.replace;
    }
}
