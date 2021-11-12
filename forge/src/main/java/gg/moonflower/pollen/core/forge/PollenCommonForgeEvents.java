package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.EventDispatcher;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvent;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvent;
import gg.moonflower.pollen.api.event.events.player.InteractEvent;
import gg.moonflower.pollen.core.Pollen;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID)
public class PollenCommonForgeEvents {

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.TickEvent.ServerTickEvent event) {
        switch (event.phase) {
            case START:
                PollenForge.postEvent(event, new TickEvent.ServerEvent.Pre());
                break;
            case END:
                PollenForge.postEvent(event, new TickEvent.ServerEvent.Post());
                break;
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.TickEvent.WorldTickEvent event) {
        switch (event.phase) {
            case START:
                PollenForge.postEvent(event, new TickEvent.LevelEvent.Pre(event.world));
                break;
            case END:
                PollenForge.postEvent(event, new TickEvent.LevelEvent.Post(event.world));
                break;
        }
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStartingEvent event) {
        PollenForge.postEvent(event, new ServerLifecycleEvent.Starting(event.getServer()));
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStartedEvent event) {
        PollenForge.postEvent(event, new ServerLifecycleEvent.Started(event.getServer()));
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStoppingEvent event) {
        PollenForge.postEvent(event, new ServerLifecycleEvent.Stopping(event.getServer()));
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStoppedEvent event) {
        PollenForge.postEvent(event, new ServerLifecycleEvent.Stopped(event.getServer()));
    }

    @SubscribeEvent
    public static void onEvent(PlayerInteractEvent.RightClickItem event) {
        InteractEvent.UseItem pollenEvent = new InteractEvent.UseItem(event.getPlayer(), event.getWorld(), event.getHand());
        EventDispatcher.post(pollenEvent);
        if (pollenEvent.isCancelled()) {
            event.setCanceled(pollenEvent.isCancelled());
            event.setCancellationResult(pollenEvent.getResult());
        }
    }

    @SubscribeEvent
    public static void onEvent(PlayerInteractEvent.RightClickBlock event) {
        InteractEvent.UseBlock pollenEvent = new InteractEvent.UseBlock(event.getPlayer(), event.getWorld(), event.getHand(), event.getHitVec());
        EventDispatcher.post(pollenEvent);
        if (pollenEvent.isCancelled()) {
            event.setCanceled(pollenEvent.isCancelled());
            event.setCancellationResult(pollenEvent.getResult());
        }
    }

    @SubscribeEvent
    public static void onEvent(PlayerInteractEvent.LeftClickBlock event) {
        InteractEvent.AttackBlock pollenEvent = new InteractEvent.AttackBlock(event.getPlayer(), event.getWorld(), event.getHand(), event.getPos(), event.getFace());
        EventDispatcher.post(pollenEvent);
        if (pollenEvent.isCancelled()) {
            event.setCanceled(pollenEvent.isCancelled());
            event.setCancellationResult(pollenEvent.getResult());
        }
    }

    @SubscribeEvent
    public static void onEvent(PlayerInteractEvent.EntityInteract event) {
        InteractEvent.UseEntity pollenEvent = new InteractEvent.UseEntity(event.getPlayer(), event.getWorld(), event.getHand(), event.getEntity());
        EventDispatcher.post(pollenEvent);
        if (pollenEvent.isCancelled()) {
            event.setCanceled(pollenEvent.isCancelled());
            event.setCancellationResult(pollenEvent.getResult());
        }
    }
}
