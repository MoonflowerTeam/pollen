package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.events.entity.player.ContainerEvents;
import net.fabricmc.fabric.impl.container.ContainerProviderImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ContainerProviderImpl.class)
public class ContainerProviderImplMixin {

    @Inject(method = "openContainer(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V", at = @At("TAIL"))
    public void openContainer(ResourceLocation identifier, ServerPlayer player, Consumer<FriendlyByteBuf> writer, CallbackInfo ci) {
        ContainerEvents.OPEN.invoker().open(player, player.containerMenu);
    }
}
