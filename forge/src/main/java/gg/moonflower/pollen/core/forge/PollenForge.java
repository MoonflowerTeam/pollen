package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.EventDispatcher;
import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.core.Pollen;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod(Pollen.MOD_ID)
public class PollenForge {

    public PollenForge() {
        Pollen.PLATFORM.setup();
    }

    public static void postEvent(Event event, PollinatedEvent pollinatedEvent) {
        boolean result = EventDispatcher.post(pollinatedEvent);
        if (event.isCancelable())
            event.setCanceled(result);
    }
}
