package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.api.client.model.ItemOverrideModifierManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ModelManager.class)
public class ModelManagerMixin {

    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/client/resources/model/ModelBakery;", at = @At("HEAD"))
    public void help(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<ModelBakery> cir) {
        ItemOverrideModifierManager.getReloadListener().reload(CompletableFuture::completedFuture, resourceManager, profiler, profiler, Runnable::run, Runnable::run);
    }
}
