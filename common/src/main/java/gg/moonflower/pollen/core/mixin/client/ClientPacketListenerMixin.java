package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.api.event.events.client.resource.ClientTagUpdateEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleUpdateTags", at = @At("TAIL"))
    public void updateTags(ClientboundUpdateTagsPacket packet, CallbackInfo ci) {
        ClientTagUpdateEvent.EVENT.invoker().onTagsReloaded();
    }
}
