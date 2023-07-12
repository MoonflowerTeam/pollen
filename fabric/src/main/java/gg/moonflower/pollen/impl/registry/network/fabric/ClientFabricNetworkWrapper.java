package gg.moonflower.pollen.impl.registry.network.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ClientFabricNetworkWrapper implements FabricNetworkWrapper {

    @Override
    public void registerLogin(ResourceLocation channelName, LoginHandler handler) {
        ClientLoginNetworking.registerGlobalReceiver(channelName, (client, clientHandler, buf, listenerAdder) -> handler.receive(clientHandler, buf, listenerAdder));
    }

    @Override
    public void registerPlay(ResourceLocation channelName, PlayHandler handler) {
        ClientPlayNetworking.registerGlobalReceiver(channelName, (client, clientHandler, buf, responseSender) -> handler.receive(clientHandler, buf, responseSender));
    }
}
