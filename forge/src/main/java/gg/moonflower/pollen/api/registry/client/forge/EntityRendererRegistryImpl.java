package gg.moonflower.pollen.api.registry.client.forge;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@ApiStatus.Internal
public class EntityRendererRegistryImpl {

    private static final Set<Consumer<EntityRenderersEvent.RegisterRenderers>> ENTITY_FACTORIES = new HashSet<>();

    @SubscribeEvent
    public static void onEvent(EntityRenderersEvent.RegisterRenderers event) {
        ENTITY_FACTORIES.forEach(consumer -> consumer.accept(event));
    }

    public static <T extends Entity> void register(EntityType<T> type, EntityRendererProvider<T> factory) {
        ENTITY_FACTORIES.add(event -> event.registerEntityRenderer(type, factory));
    }
}
