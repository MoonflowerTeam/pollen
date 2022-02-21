package gg.moonflower.pollen.core.mixin;

import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.datagen.provider.ConditionalDataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(TagsProvider.class)
public class TagsProviderMixin<T> {

    @Unique
    private ResourceLocation id;

    @Inject(method = "lambda$run$1", at = @At("HEAD"))
    public void captureId(HashCache hashCache, ResourceLocation id, Tag.Builder builder, CallbackInfo ci) {
        this.id = id;
    }

    @ModifyVariable(method = "lambda$run$1", ordinal = 0, at = @At(value = "STORE"))
    public JsonObject modifyJson(JsonObject value) {
        if (this instanceof ConditionalDataProvider)
            ((ConditionalDataProvider) this).injectConditions(this.id, value);
        return value;
    }
}
