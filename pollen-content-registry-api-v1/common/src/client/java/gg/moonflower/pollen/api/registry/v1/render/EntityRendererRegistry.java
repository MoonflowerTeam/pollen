package gg.moonflower.pollen.api.registry.v1.render;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.base.platform.Platform;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

/**
 * @author Jackson
 * @since 1.0.0
 */
public interface EntityRendererRegistry {

    /**
     * Registers a renderer for the specified entity type.
     *
     * @param type    The type of entity to register for
     * @param factory The factory for creating a new renderer
     * @param <T>     The type of entity to make a renderer for
     */
    @ExpectPlatform
    static <T extends Entity> void register(Supplier<EntityType<T>> type, EntityRendererProvider<T> factory) {
        Platform.error();
    }

    /**
     * Registers a definition factory for a model layer.
     *
     * @param layerLocation The location of the layer
     * @param supplier The factory for making a new definition
     */
    @ExpectPlatform
    static void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
        Platform.error();
    }
}
