package gg.moonflower.pollen.api.platform.forge;

import gg.moonflower.pollen.api.platform.PlatformInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ForgePlatformInstance extends PlatformInstance {
    private final IEventBus eventBus;

    protected ForgePlatformInstance(String modId, IEventBus eventBus, Runnable commonInit, Runnable clientInit, Runnable commonPostInit, Runnable clientPostInit, Runnable commonNetworkInit, Runnable clientNetworkInit) {
        super(modId);
        this.eventBus = eventBus;
        this.eventBus.<FMLCommonSetupEvent>addListener(e -> e.enqueueWork(commonPostInit));
        this.eventBus.<FMLClientSetupEvent>addListener(e -> e.enqueueWork(clientPostInit));

        commonInit.run();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> clientInit::run);

        commonNetworkInit.run();
        clientNetworkInit.run();
    }

    public IEventBus getEventBus() {
        return eventBus;
    }
}
