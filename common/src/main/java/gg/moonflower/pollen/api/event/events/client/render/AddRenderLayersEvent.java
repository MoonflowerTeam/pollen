package gg.moonflower.pollen.api.event.events.client.render;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Fired each time layers need to be added to living entity renderers.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface AddRenderLayersEvent {

    PollinatedEvent<AddRenderLayersEvent> EVENT = EventRegistry.createLoop(AddRenderLayersEvent.class);

    /**
     * Adds layers to entity renderers.
     *
     * @param context The context for entity renderers
     */
    void addLayers(Context context);

    /**
     * @author Ocelot
     * @since 1.0.0
     */
    interface Context {

        /**
         * @return The list of skin types
         */
        Set<String> getSkins();

        /**
         * Returns a player skin renderer. Vanilla skins are <code>default</code> and <code>slim</code>.
         *
         * @param skinName The name of the skin renderer to return
         * @return The skin renderer, or <code>null</code> if not found
         */
        @Nullable
        PlayerRenderer getSkin(String skinName);

        /**
         * Retrieves an entity renderer for an entity type.
         *
         * @param entityType The entity to return a renderer for
         * @param <T>        The type of entity the renderer is for
         * @param <R>        The renderer type
         * @return The renderer for the specified entity type
         */
        @Nullable <T extends LivingEntity, R extends LivingEntityRenderer<T, ? extends EntityModel<T>>> R getRenderer(EntityType<? extends T> entityType);

        /**
         * @return The entity model set instance
         */
        EntityModelSet getEntityModels();
    }
}
