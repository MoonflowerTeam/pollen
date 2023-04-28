package gg.moonflower.pollen.impl.event.network.forge;

import gg.moonflower.pollen.api.event.network.v1.ClientNetworkEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientNetworkEventImpl {

    @SubscribeEvent
    public static void onEvent(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientNetworkEvent.LOGIN.invoker().event(event.getMultiPlayerGameMode(), event.getPlayer(), event.getConnection());
    }

    @SubscribeEvent
    public static void onEvent(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientNetworkEvent.DISCONNECT.invoker().event(event.getMultiPlayerGameMode(), event.getPlayer(), event.getConnection());
    }
}
