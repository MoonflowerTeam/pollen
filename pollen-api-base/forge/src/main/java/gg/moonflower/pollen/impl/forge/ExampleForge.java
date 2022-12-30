package gg.moonflower.pollen.impl.forge;

import gg.moonflower.pollen.impl.BaseApiInitializer;
import gg.moonflower.pollen.impl.base.BaseApiClientInitializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BaseApiInitializer.MOD_ID)
public class ExampleForge {

    public ExampleForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::initClient);
    }

    private void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(BaseApiClientInitializer::init);
    }
}
