package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.events.client.render.InitRendererEvent;
import gg.moonflower.pollen.api.event.events.registry.client.ParticleFactoryRegistryEvent;
import gg.moonflower.pollen.api.sync.forge.SyncedDataManagerImpl;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod(Pollen.MOD_ID)
public class PollenForge {

    public PollenForge() {
        Pollen.PLATFORM.setup();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(PollenForge::init);
        modBus.addListener(PollenForge::registerParticles);
        modBus.addListener(EventPriority.NORMAL, true, ColorHandlerEvent.Block.class, event -> InitRendererEvent.EVENT.invoker().initRenderer());
    }

    private static void init(FMLCommonSetupEvent event) {
        SyncedDataManagerImpl.init();
    }

    private static void registerParticles(ParticleFactoryRegisterEvent event) {
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        ParticleFactoryRegistryEvent.EVENT.invoker().registerParticles(new ParticleFactoryRegistryEvent.Registry() {
            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
                particleEngine.register(type, provider);
            }

            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleFactoryRegistryEvent.Factory<T> factory) {
                particleEngine.register(type, factory::create);
            }
        });
    }
}
