package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.events.client.render.InitRendererEvent;
import gg.moonflower.pollen.api.registry.client.ShaderRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;

@ApiStatus.Internal
@Mod(Pollen.MOD_ID)
public class PollenForge {

    public PollenForge() {
        Pollen.PLATFORM.setup();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(PollenForge::init);
        modBus.addListener(EventPriority.NORMAL, true, ColorHandlerEvent.Block.class, event -> InitRendererEvent.EVENT.invoker().initRenderer());
        modBus.<RegisterShadersEvent>addListener(event -> {
            Logger logger = LogManager.getLogger();
            ShaderRegistry.getRegisteredShaders().forEach(entry -> {
                try {
                    event.registerShader(new ShaderInstance(event.getResourceManager(), entry.getKey(), entry.getValue()), instance -> ShaderRegistry.loadShader(entry.getKey(), instance));
                } catch (IOException e) {
                    logger.error("Failed to load shader: " + entry.getKey(), e);
                }
            });
        });
    }

    private static void init(FMLCommonSetupEvent event) {
    }
}
