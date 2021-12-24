package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.registry.resource.forge.ResourceRegistryImpl;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
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
public class SimpleReloadableResourceManagerMixin {

    @Final
    @Shadow
    private PackType type;

    @Shadow
    @Final
    private List<PreparableReloadListener> listeners;

    @Inject(at = @At("HEAD"), method = "createReload")
    private void reload(Executor executor, Executor executor2, CompletableFuture<Unit> completableFuture, List<PackResources> list, CallbackInfoReturnable<CompletableFuture<Unit>> info) {
        ResourceRegistryImpl.inject(this.type, this.listeners);
    }
}
