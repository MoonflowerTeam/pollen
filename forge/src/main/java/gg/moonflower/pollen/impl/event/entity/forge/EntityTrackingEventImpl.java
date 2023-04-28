package gg.moonflower.pollen.impl.event.entity.forge;

import gg.moonflower.pollen.api.event.entity.v1.EntityTrackingEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityTrackingEventImpl {

    @SubscribeEvent
    public static void onEvent(PlayerEvent.StartTracking event) {
        EntityTrackingEvent.START_TRACKING.invoker().event(event.getTarget(), event.getEntity());
    }

    @SubscribeEvent
    public static void onEvent(PlayerEvent.StopTracking event) {
        EntityTrackingEvent.STOP_TRACKING.invoker().event(event.getTarget(), event.getEntity());
    }
}
