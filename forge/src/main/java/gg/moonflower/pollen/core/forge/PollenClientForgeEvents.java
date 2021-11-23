package gg.moonflower.pollen.core.forge;

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
                TickEvent.CLIENT_PRE.invoker().tick();
                break;
            case END:
                TickEvent.CLIENT_POST.invoker().tick();
                break;
        }
    }

    @SubscribeEvent
    public static void onEvent(ClientPlayerNetworkEvent.LoggedInEvent event) {
        ClientNetworkEvent.LOGIN.invoker().login(event.getController(), event.getPlayer(), event.getNetworkManager());
    }

    @SubscribeEvent
    public static void onEvent(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientNetworkEvent.LOGOUT.invoker().logout(event.getController(), event.getPlayer(), event.getNetworkManager());
    }

    @SubscribeEvent
    public static void onEvent(ClientPlayerNetworkEvent.RespawnEvent event) {
        ClientNetworkEvent.RESPAWN.invoker().respawn(event.getController(), event.getOldPlayer(), event.getPlayer(), event.getNetworkManager());
    }
}
