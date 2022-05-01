package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

/**
 * @author Jackson
 * @since 1.0.0
 */
public final class EntityRendererRegistry {

    private EntityRendererRegistry() {
    }

    /**
     * Registers a renderer for the specified entity type.
     *
     * @param type    The type of entity to register for
     * @param factory The factory for creating a new renderer
     * @param <T>     The type of entity to make a renderer for
     */
    @ExpectPlatform
    public static <T extends Entity> void register(Supplier<EntityType<T>> type, EntityRendererFactory<T> factory) {
        Platform.error();
    }

    @FunctionalInterface
    public interface EntityRendererFactory<T extends Entity> {

        /**
         * Creates a new entity renderer with context.
         *
         * @param context The context for creation
         * @return A new renderer
         */
        EntityRenderer<T> create(Context context);

        interface Context {

            /**
             * @return The entity renderer instance
             */
            EntityRenderDispatcher getEntityRenderDispatcher();

            /**
             * @return The item renderer instance
             */
            ItemRenderer getItemRenderer();

            /**
             * @return The client resource manager
             */
            ResourceManager getResourceManager();

            /**
             * @return The main client font
             */
            Font getFont();
        }
    }
}
