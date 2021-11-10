package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.events.TickEvent;
import gg.moonflower.pollen.core.Pollen;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
}
