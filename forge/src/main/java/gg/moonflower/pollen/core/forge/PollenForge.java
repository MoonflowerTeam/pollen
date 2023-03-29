package gg.moonflower.pollen.core.forge;

import dev.architectury.platform.forge.EventBuses;
import gg.moonflower.pollen.core.PollenClient;
import gg.moonflower.pollen.core.Pollen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Pollen.MOD_ID)
public class PollenForge {

    public PollenForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Pollen.MOD_ID, eventBus);
        eventBus.addListener(this::commonInit);
        eventBus.addListener(this::clientInit);

        Pollen.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> PollenClient::init);
    }

    private void commonInit(FMLCommonSetupEvent event) {
        Pollen.postInit();
    }

    private void clientInit(FMLClientSetupEvent event) {
        PollenClient.postInit();
    }
}
