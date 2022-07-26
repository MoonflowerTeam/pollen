package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.events.client.render.AddRenderLayersEvent;
import gg.moonflower.pollen.api.event.events.client.render.InitRendererEvent;
import gg.moonflower.pollen.api.event.events.registry.client.ParticleFactoryRegistryEvent;
import gg.moonflower.pollen.api.event.events.registry.client.RegisterAtlasSpriteEvent;
import gg.moonflower.pollen.api.registry.client.ShaderRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Set;

@ApiStatus.Internal
@Mod(Pollen.MOD_ID)
public class PollenForge {

    public PollenForge() {
        Pollen.PLATFORM.setup();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(PollenForge::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modBus.addListener(EventPriority.NORMAL, true, RegisterColorHandlersEvent.Block.class, event -> InitRendererEvent.EVENT.invoker().initRenderer());
            modBus.addListener(PollenForge::registerParticles);
            modBus.addListener(PollenForge::registerSprites);
            modBus.<EntityRenderersEvent.AddLayers>addListener(event -> {
                AddRenderLayersEvent.EVENT.invoker().addLayers(new AddRenderLayersEvent.Context() {
                    @Override
                    public Set<String> getSkins() {
                        return event.getSkins();
                    }

                    @Nullable
                    @Override
                    public PlayerRenderer getSkin(String skinName) {
                        return event.getSkin(skinName);
                    }

                    @Override
                    public <T extends LivingEntity, R extends LivingEntityRenderer<T, ? extends EntityModel<T>>> @Nullable R getRenderer(EntityType<? extends T> entityType) {
                        return event.getRenderer(entityType);
                    }

                    @Override
                    public EntityModelSet getEntityModels() {
                        return event.getEntityModels();
                    }
                });
            });
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
        });
    }

    private static void init(FMLCommonSetupEvent event) {
    }

    private static void registerSprites(TextureStitchEvent.Pre event) {
        TextureAtlas atlas = event.getAtlas();
        RegisterAtlasSpriteEvent.event(atlas.location()).invoker().registerSprites(atlas, event::addSprite);
    }

    private static void registerParticles(RegisterParticleProvidersEvent event) {
        ParticleFactoryRegistryEvent.EVENT.invoker().registerParticles(new ParticleFactoryRegistryEvent.Registry() {
            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
                event.register(type, provider);
            }

            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleFactoryRegistryEvent.Factory<T> factory) {
                event.register(type, factory::create);
            }
        });
    }
}
