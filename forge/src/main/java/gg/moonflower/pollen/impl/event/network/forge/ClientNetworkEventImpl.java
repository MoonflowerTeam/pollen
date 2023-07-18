package gg.moonflower.pollen.impl.event.network.forge;

import gg.moonflower.pollen.api.event.network.v1.ClientNetworkEvent;
import gg.moonflower.pollen.core.Pollen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, value = Dist.CLIENT)
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
