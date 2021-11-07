package gg.moonflower.pollen.api.platform.forge;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ForgePlatform extends Platform {

    private final IEventBus eventBus;

    ForgePlatform(String modId, IEventBus eventBus, Runnable commonInit, Runnable clientInit, Runnable commonPostInit, Runnable clientPostInit) {
        super(modId);
        this.eventBus = eventBus;
        this.eventBus.<FMLCommonSetupEvent>addListener(e -> e.enqueueWork(commonPostInit));
        this.eventBus.<FMLClientSetupEvent>addListener(e -> e.enqueueWork(clientPostInit));

        commonInit.run();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> clientInit::run);
    }

    public IEventBus getEventBus() {
        return eventBus;
    }
}
