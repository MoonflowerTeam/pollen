package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.registry.resource.ReloadStartListener;
import gg.moonflower.pollen.core.extensions.InjectableResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(SimpleReloadableResourceManager.class)
public class SimpleReloadableResourceManagerMixin implements InjectableResourceManager {

    @Shadow
    @Final
    private List<PreparableReloadListener> recentlyRegistered;

    @Shadow
    @Final
    private List<PreparableReloadListener> listeners;

    @Override
    public void pollen_registerReloadListenerFirst(PreparableReloadListener preparableReloadListener) {
        this.listeners.add(0, preparableReloadListener);
        this.recentlyRegistered.add(preparableReloadListener);
    }

    @Inject(method = "createReload", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;isDebugEnabled()Z", shift = At.Shift.BEFORE))
    public void onReload(Executor backgroundExecutor, Executor gameExecutor, List<PreparableReloadListener> list, CompletableFuture<Unit> completableFuture, CallbackInfoReturnable<ReloadInstance> cir) {
        this.listeners.forEach(listener -> {
            if (listener instanceof ReloadStartListener)
                ((ReloadStartListener) listener).onReloadStart((ResourceManager) this, backgroundExecutor, gameExecutor);
        });
    }
}
