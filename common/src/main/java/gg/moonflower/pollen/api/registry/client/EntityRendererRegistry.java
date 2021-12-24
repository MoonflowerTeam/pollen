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

public final class EntityRendererRegistry {

    private EntityRendererRegistry() {
    }

    @ExpectPlatform
    public static <T extends Entity> void register(EntityType<T> type, EntityRendererFactory<T> factory) {
        Platform.error();
    }

    @FunctionalInterface
    public interface EntityRendererFactory<T extends Entity> {
        EntityRenderer<T> create(Context context);

        interface Context {

            EntityRenderDispatcher getEntityRenderDispatcher();

            ItemRenderer getItemRenderer();

            ResourceManager getResourceManager();

            Font getFont();
        }
    }
}
