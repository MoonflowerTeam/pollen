package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.resources.forge.ResourceRegistryImpl;
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

    @Inject(at = @At("HEAD"), method = "createReload")
    private void reload(Executor var1, Executor var2, List<PreparableReloadListener> listeners, CompletableFuture<Unit> future, CallbackInfoReturnable<CompletableFuture<Unit>> info) {
        ResourceRegistryImpl.inject(this.type, listeners);
    }
}
