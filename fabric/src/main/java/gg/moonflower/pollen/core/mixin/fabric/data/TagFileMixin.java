package gg.moonflower.pollen.core.mixin.fabric.data;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.resource.condition.ConditionalTagEntry;
import net.minecraft.tags.TagFile;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TagFile.class)
public class TagFileMixin {


    @ModifyReceiver(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;listOf()Lcom/mojang/serialization/Codec;"))
    private static Codec<?> modifyCodec(Codec<?> receiver) {
        return ConditionalTagEntry.FULL_CODEC;
    }
}

//TAG_FILE_CODEC