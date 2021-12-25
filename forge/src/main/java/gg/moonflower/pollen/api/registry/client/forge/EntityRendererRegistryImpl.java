package gg.moonflower.pollen.api.registry.client.forge;

import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRendererRegistryImpl {

    private static final Set<Consumer<EntityRenderersEvent.RegisterRenderers>> ENTITY_FACTORIES = ConcurrentHashMap.newKeySet();
    private static final Set<Consumer<EntityRenderersEvent.RegisterLayerDefinitions>> LAYER_DEFINITION_FACTORIES = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public static void onEvent(EntityRenderersEvent.RegisterRenderers event) {
        ENTITY_FACTORIES.forEach(consumer -> consumer.accept(event));
    }

    @SubscribeEvent
    public static void onEvent(EntityRenderersEvent.RegisterLayerDefinitions event) {
        LAYER_DEFINITION_FACTORIES.forEach(consumer -> consumer.accept(event));
    }

    public static <T extends Entity> void register(Supplier<EntityType<T>> type, EntityRendererProvider<T> factory) {
        ENTITY_FACTORIES.add(event -> event.registerEntityRenderer(type.get(), factory));
    }

    public static void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
        LAYER_DEFINITION_FACTORIES.add(event -> event.registerLayerDefinition(layerLocation, supplier));
    }
}
