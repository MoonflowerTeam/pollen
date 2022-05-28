package gg.moonflower.pollen.core.mixin.loot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifierManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootTables;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
}
