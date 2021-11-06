package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.EventDispatcher;
import gg.moonflower.pollen.core.Pollen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, value = Dist.CLIENT)
public class PollenClientForgeEvents {

    @SubscribeEvent
    public static void onEvent(TickEvent.ClientTickEvent event) {
        switch (event.phase) {
            case START:
                EventDispatcher.post(new gg.moonflower.pollen.api.event.events.TickEvent.ClientEvent.Pre());
                break;
            case END:
                EventDispatcher.post(new gg.moonflower.pollen.api.event.events.TickEvent.ClientEvent.Post());
                break;
        }
    }
}
