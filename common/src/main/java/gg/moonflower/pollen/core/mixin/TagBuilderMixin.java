package gg.moonflower.pollen.core.mixin;

import com.google.gson.JsonElement;
import gg.moonflower.pollen.api.registry.ResourceConditionRegistry;
import gg.moonflower.pollen.api.resource.condition.ConditionalTagEntry;
import gg.moonflower.pollen.core.extensions.TagBuilderExtension;
import net.minecraft.tags.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "parseEntry", at = @At("RETURN"), cancellable = true)
    private static void modifyEntry(JsonElement jsonElement, CallbackInfoReturnable<Tag.Entry> cir) {
        if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has(ResourceConditionRegistry.getConditionsKey()))
            cir.setReturnValue(new ConditionalTagEntry(cir.getReturnValue(), jsonElement.getAsJsonObject()));
    }
}
