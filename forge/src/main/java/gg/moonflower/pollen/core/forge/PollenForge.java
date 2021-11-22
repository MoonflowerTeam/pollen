package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.EventDispatcher;
import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.sync.forge.SyncedDataManagerImpl;
import gg.moonflower.pollen.core.Pollen;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod(Pollen.MOD_ID)
public class PollenForge {

    public PollenForge() {
        Pollen.PLATFORM.setup();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PollenForge::init);
    }

    private static void init(FMLCommonSetupEvent event) {
    }

    public static void postEvent(Event event, PollinatedEvent pollinatedEvent) {
        boolean result = EventDispatcher.post(pollinatedEvent);
        if (event.isCancelable())
            event.setCanceled(result);
    }
}
