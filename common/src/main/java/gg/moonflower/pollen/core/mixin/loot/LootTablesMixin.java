package gg.moonflower.pollen.core.mixin.loot;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifierManager;
import gg.moonflower.pollen.api.resource.modifier.type.LootModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LootTables.class)
public abstract class LootTablesMixin extends SimpleJsonResourceReloadListener {

    private LootTablesMixin(Gson gson, String string) {
        super(gson, string);
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, JsonElement> object = super.prepare(resourceManager, profiler);
        ResourceModifierManager.getServerCompleteFuture().join(); // Wait for server modifiers before completing
        return object;
    }

    @Inject(method = "lambda$apply$0", at = @At(value = "INVOKE", target = "Lcom/google/gson/Gson;fromJson(Lcom/google/gson/JsonElement;Ljava/lang/Class;)Ljava/lang/Object;", shift = At.Shift.BEFORE))
    private static void preDeserialize(ImmutableMap.Builder<ResourceLocation, LootTable> builder, ResourceLocation resourceLocation, JsonElement jsonElement, CallbackInfo ci) {
        LootModifier.loadingId = resourceLocation;
    }

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    public void clearId(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        LootModifier.loadingId = null;
    }
}
