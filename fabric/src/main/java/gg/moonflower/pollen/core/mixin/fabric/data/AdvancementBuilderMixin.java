package gg.moonflower.pollen.core.mixin.fabric.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.event.events.AdvancementConstructingEvent;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(Advancement.Builder.class)
public class AdvancementBuilderMixin {

    @Invoker("<init>")
    private static Advancement.Builder invokeInit(@Nullable ResourceLocation resourceLocation, @Nullable DisplayInfo displayInfo, AdvancementRewards advancementRewards, Map<String, Criterion> map, String[][] strings) {
        throw new AssertionError();
    }

    @Inject(method = "fromJson", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void modifyBuilder(JsonObject json, DeserializationContext context, CallbackInfoReturnable<Advancement.Builder> info, ResourceLocation resourcelocation, DisplayInfo displayinfo, AdvancementRewards rewards, Map<String, Criterion> map, JsonArray jsonarray, String[][] astring) {
        Advancement.Builder builder = invokeInit(resourcelocation, displayinfo, rewards, map, astring);
        AdvancementConstructingEvent.EVENT.invoker().modifyAdvancement(builder, context);
        info.setReturnValue(builder);
    }
}
