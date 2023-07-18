package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.registry.v1.RegisterAtlasSpriteEvent;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PollenForgeClient {

    @SubscribeEvent
    public static void registerSprites(TextureStitchEvent.Pre event) {
        TextureAtlas atlas = event.getAtlas();
        RegisterAtlasSpriteEvent.event(atlas.location()).invoker().registerSprites(atlas, event::addSprite);
    }
}
