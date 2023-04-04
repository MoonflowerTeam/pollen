package gg.moonflower.pollen.core.forge;

import dev.architectury.platform.forge.EventBuses;
import gg.moonflower.pollen.api.event.registry.v1.RegisterAtlasSpriteEvent;
import gg.moonflower.pollen.core.PollenClient;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.impl.event.entity.ModifyTradesEventsImpl;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
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
        eventBus.addListener(this::registerSprites);

        MinecraftForge.EVENT_BUS.register(ModifyTradesEventsImpl.class);

        Pollen.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> PollenClient::init);
    }

    private void commonInit(FMLCommonSetupEvent event) {
        Pollen.postInit();
    }

    private void clientInit(FMLClientSetupEvent event) {
        PollenClient.postInit();
    }

    private void registerSprites(TextureStitchEvent.Pre event) {
        TextureAtlas atlas = event.getAtlas();
        RegisterAtlasSpriteEvent.event(atlas.location()).invoker().registerSprites(atlas, event::addSprite);
    }
}
