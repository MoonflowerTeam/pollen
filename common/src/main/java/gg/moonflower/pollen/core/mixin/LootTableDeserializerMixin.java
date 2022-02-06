package gg.moonflower.pollen.core.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.event.events.LootTableConstructingEvent;
import gg.moonflower.pollen.api.resource.modifier.type.LootModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(LootTable.Serializer.class)
public class LootTableDeserializerMixin {

    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/world/level/storage/loot/LootTable;", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void modifyLootTable(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<LootTable> cir, JsonObject json) {
        LootTable source = cir.getReturnValue();
        LootTableAccessor accessor = (LootTableAccessor) source;

        AtomicReference<LootContextParamSet> paramSet = new AtomicReference<>(source.getParamSet());
        List<LootPool> addPools = new ArrayList<>();
        List<LootItemFunction> addFunctions = new ArrayList<>();

        LootTableConstructingEvent.EVENT.invoker().modifyLootTable(new LootTableConstructingEvent.Context() {
            @Override
            public void addPool(LootPool lootPool) {
                addPools.add(lootPool);
            }

            @Override
            public void addFunction(LootItemFunction function) {
                addFunctions.add(function);
            }

            @Override
            public void setParamSet(LootContextParamSet parameterSet) {
                paramSet.set(parameterSet);
            }

            @Override
            public LootTable getLootTable() {
                return source;
            }

            @Override
            public JsonObject getJson() {
                return json;
            }

            @Override
            public JsonDeserializationContext getJsonContext() {
                return jsonDeserializationContext;
            }

            @Override
            public ResourceLocation getId() {
                return LootModifier.loadingId;
            }
        });

        if (!addPools.isEmpty() || !addFunctions.isEmpty() || paramSet.get() != source.getParamSet()) {
            LootPool[] sourcePools = accessor.getPools();
            LootItemFunction[] sourceFunctions = accessor.getFunctions();
            LootPool[] pools = new LootPool[sourcePools.length + addPools.size()];
            LootItemFunction[] functions = new LootItemFunction[accessor.getFunctions().length + addFunctions.size()];

            System.arraycopy(sourcePools, 0, pools, 0, sourcePools.length);
            for (int i = 0; i < addPools.size(); i++)
                pools[sourcePools.length + i] = addPools.get(i);

            System.arraycopy(sourceFunctions, 0, functions, 0, sourceFunctions.length);
            for (int i = 0; i < addFunctions.size(); i++)
                functions[sourceFunctions.length + i] = addFunctions.get(i);

            cir.setReturnValue(LootTableAccessor.init(paramSet.get(), pools, functions));
        }
    }
}
