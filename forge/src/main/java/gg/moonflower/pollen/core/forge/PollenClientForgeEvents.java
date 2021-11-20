package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.EventDispatcher;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvent;
import gg.moonflower.pollen.api.event.events.network.ClientNetworkEvent;
import gg.moonflower.pollen.core.Pollen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, value = Dist.CLIENT)
public class PollenClientForgeEvents {

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
        switch (event.phase) {
            case START:
                EventDispatcher.post(new TickEvent.ClientEvent.Pre());
                break;
            case END:
                EventDispatcher.post(new TickEvent.ClientEvent.Post());
                break;
        }
    }

    @SubscribeEvent
    public static void onEvent(ClientPlayerNetworkEvent.LoggedInEvent event) {
        PollenForge.postEvent(event, new ClientNetworkEvent.LoggedIn(event.getController(), event.getPlayer(), event.getNetworkManager()));
    }

    @SubscribeEvent
    public static void onEvent(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        PollenForge.postEvent(event, new ClientNetworkEvent.LoggedOut(event.getController(), event.getPlayer(), event.getNetworkManager()));
    }

    @SubscribeEvent
    public static void onEvent(ClientPlayerNetworkEvent.RespawnEvent event) {
        PollenForge.postEvent(event, new ClientNetworkEvent.Respawn(event.getController(), event.getOldPlayer(), event.getPlayer(), event.getNetworkManager()));
    }
}
