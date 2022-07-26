package gg.moonflower.pollen.core.mixin.forge;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Map;

@Mixin(SimpleJsonResourceReloadListener.class)
public class SimpleJsonResourceReloadListenerMixin {

    @Shadow
    @Final
    private String directory;

    @SuppressWarnings("ConstantConditions")
    @Inject(at = @At("RETURN"), method = "prepare")
    public void applyResourceConditions(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<Map<ResourceLocation, JsonElement>> cir) {
        if ((Object) this instanceof RecipeManager) // Forge already handles recipes
            return;

        profiler.push(String.format("Pollen resource conditions: %s", this.directory));

        Iterator<Map.Entry<ResourceLocation, JsonElement>> it = cir.getReturnValue().entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<ResourceLocation, JsonElement> entry = it.next();
            JsonElement resourceData = entry.getValue();

            if (resourceData.isJsonObject() && !CraftingHelper.processConditions(resourceData.getAsJsonObject(), "conditions", ICondition.IContext.EMPTY))
                it.remove();
        }

        profiler.pop();
    }
}
