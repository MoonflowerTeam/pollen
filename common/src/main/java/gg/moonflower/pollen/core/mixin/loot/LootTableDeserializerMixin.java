package gg.moonflower.pollen.core.mixin.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.event.events.LootTableConstructingEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Type;

@Mixin(LootTable.Serializer.class)
public class LootTableDeserializerMixin {

    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/world/level/storage/loot/LootTable;", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void modifyLootTable(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<LootTable> cir, JsonObject json) {
        LootTableConstructingEvent.Context context = new LootTableConstructingEvent.Context(cir.getReturnValue(), json, jsonDeserializationContext);
        LootTableConstructingEvent.EVENT.invoker().modifyLootTable(context);
        cir.setReturnValue(context.apply());
    }
}
