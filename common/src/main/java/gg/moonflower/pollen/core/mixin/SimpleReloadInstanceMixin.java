package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.registry.resource.ReloadStartListener;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(SimpleReloadInstance.class)
public class SimpleReloadInstanceMixin {

    @Inject(method = "of", at = @At("HEAD"))
    private static void of(ResourceManager resourceManager, List<PreparableReloadListener> listeners, Executor backgroundExecutor, Executor gameExecutor, CompletableFuture<Unit> completableFuture, CallbackInfoReturnable<SimpleReloadInstance<Void>> cir) {
        listeners.forEach(listener -> {
            if (listener instanceof ReloadStartListener)
                ((ReloadStartListener) listener).onReloadStart(resourceManager, backgroundExecutor, gameExecutor);
        });
    }
}
