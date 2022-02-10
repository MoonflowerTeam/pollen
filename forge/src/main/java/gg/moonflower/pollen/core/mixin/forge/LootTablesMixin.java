package gg.moonflower.pollen.core.mixin.forge;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.event.events.LootTableConstructingEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(LootTables.class)
public class LootTablesMixin {

    @Shadow
    private Map<ResourceLocation, LootTable> tables;

    @Inject(method = "apply", at = @At("RETURN"))
    private void apply(Map<ResourceLocation, JsonObject> objectMap, ResourceManager manager, ProfilerFiller profiler, CallbackInfo info) {
        Map<ResourceLocation, LootTable> newTables = new HashMap<>();

        this.tables.forEach((id, supplier) -> {
            LootTableConstructingEvent.Context context = new LootTableConstructingEvent.Context(id, supplier);
            LootTableConstructingEvent.EVENT.invoker().modifyLootTable(context);
            newTables.computeIfAbsent(id, __ -> context.apply());
        });

        this.tables = ImmutableMap.copyOf(newTables);
    }
}
