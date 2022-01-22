package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.events.client.render.AddRenderLayersEvent;
import gg.moonflower.pollen.api.event.events.client.render.InitRendererEvent;
import gg.moonflower.pollen.api.event.events.registry.client.ParticleFactoryRegistryEvent;
import gg.moonflower.pollen.api.event.events.registry.client.RegisterAtlasSpriteEvent;
import gg.moonflower.pollen.api.sync.forge.SyncedDataManagerImpl;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.mixin.forge.client.EntityRenderDispatcherAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

@ApiStatus.Internal
@Mod(Pollen.MOD_ID)
public class PollenForge {

    public PollenForge() {
        Pollen.PLATFORM.setup();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(PollenForge::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modBus.addListener(EventPriority.NORMAL, true, ColorHandlerEvent.Block.class, event -> InitRendererEvent.EVENT.invoker().initRenderer());
            modBus.addListener(PollenForge::clientInit);
            modBus.addListener(PollenForge::registerParticles);
            modBus.addListener(PollenForge::registerSprites);
        });
    }

    private static void clientInit(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
        {
            EntityRenderDispatcher entityRenderDispatcher = event.getMinecraftSupplier().get().getEntityRenderDispatcher();
            Map<EntityType<?>, EntityRenderer<?>> renderers = ((EntityRenderDispatcherAccessor) entityRenderDispatcher).getRenderers();
            Map<String, PlayerRenderer> playerRenderers = ((EntityRenderDispatcherAccessor) entityRenderDispatcher).getPlayerRenderers();
            AddRenderLayersEvent.EVENT.invoker().addLayers(new AddRenderLayersEvent.Context() {
                @Override
                public Set<String> getSkins() {
                    return playerRenderers.keySet();
                }

                @Nullable
                @Override
                public PlayerRenderer getSkin(String skinName) {
                    return playerRenderers.get(skinName);
                }

                @SuppressWarnings("unchecked")
                @Override
                public <T extends LivingEntity, R extends LivingEntityRenderer<T, ? extends EntityModel<T>>> @Nullable R getRenderer(EntityType<? extends T> entityType) {
                    return (R) renderers.get(entityType);
                }
            });
        });
    }

    private static void init(FMLCommonSetupEvent event) {
        SyncedDataManagerImpl.init();
    }

    private static void registerSprites(TextureStitchEvent.Pre event) {
        TextureAtlas atlas = event.getMap();
        RegisterAtlasSpriteEvent.event(atlas.location()).invoker().registerSprites(atlas, event::addSprite);
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
