//package gg.moonflower.pollen.core.mixin.fabric.data;
//
//import com.llamalad7.mixinextras.injector.ModifyReceiver;
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.DataResult;
//import com.mojang.serialization.Dynamic;
//import gg.moonflower.pollen.api.resource.condition.ConditionalTagEntry;
//import net.minecraft.tags.TagLoader;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Redirect;
//
//@Mixin(TagLoader.class)
//public class TagLoaderMixin {
//
//    @ModifyReceiver(method = "load", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/Dynamic;)Lcom/mojang/serialization/DataResult;"))
//    public DataResult<?> useConditionalCodec(Codec<?> instance, Dynamic<?> dynamic) {
//        return ConditionalTagEntry.TAG_FILE_CODEC.parse(dynamic);
//    }
//}
