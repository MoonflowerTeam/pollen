package gg.moonflower.pollen.core.mixin.forge.client;

import gg.moonflower.pollen.api.util.forge.ModResourcePackCreator;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ClientPackSource.class)
public class ClientPackSourceMixin {

    @Inject(method = "loadPacks", at = @At("RETURN"))
    private void addBuiltinResourcePacks(Consumer<Pack> consumer, Pack.PackConstructor factory, CallbackInfo ci) {
        ModResourcePackCreator.CLIENT_RESOURCE_PACK_PROVIDER.loadPacks(consumer, factory);
    }
}
