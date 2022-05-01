package gg.moonflower.pollen.api.registry.client.fabric;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public class EntityRendererRegistryImpl {

    public static <T extends Entity> void register(Supplier<EntityType<T>> type, EntityRendererProvider<T> factory) {
        EntityRendererRegistry.register(type.get(), factory);
    }

    public static void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
        EntityModelLayerRegistry.registerModelLayer(layerLocation, supplier::get);
    }
}
