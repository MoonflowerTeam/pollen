package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.resource.modifier.ResourceModifierManager;
import gg.moonflower.pollen.core.extensions.InjectableResourceManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ServerResources.class)
public class ServerResourcesMixin {

    @Inject(method = "loadResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ReloadableResourceManager;reload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Ljava/util/concurrent/CompletableFuture;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void init(List<PackResources> list, RegistryAccess registryAccess, Commands.CommandSelection commandSelection, int i, Executor executor, Executor executor2, CallbackInfoReturnable<CompletableFuture<ServerResources>> cir, ServerResources serverResources) {
        ResourceManager resourceManager = serverResources.getResourceManager();
        if (resourceManager instanceof InjectableResourceManager)
            ((InjectableResourceManager) resourceManager).pollen_registerReloadListenerFirst(ResourceModifierManager.createServerReloader(serverResources));
    }
}
