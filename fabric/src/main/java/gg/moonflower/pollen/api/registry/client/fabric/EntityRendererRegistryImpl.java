package gg.moonflower.pollen.api.registry.client.fabric;

import gg.moonflower.pollen.api.registry.client.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public class EntityRendererRegistryImpl {
    public static <T extends Entity> void register(Supplier<EntityType<T>> type, EntityRendererRegistry.EntityRendererFactory<T> factory) {
        net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry.INSTANCE.register(type.get(), (renderDispatcher, context) -> factory.create(new EntityRendererRegistry.EntityRendererFactory.Context() {

            @Override
            public EntityRenderDispatcher getEntityRenderDispatcher() {
                return renderDispatcher;
            }

            @Override
            public ItemRenderer getItemRenderer() {
                return context.getItemRenderer();
            }

            @Override
            public ResourceManager getResourceManager() {
                return context.getResourceManager();
            }

            @Override
            public Font getFont() {
                return Minecraft.getInstance().font;
            }
        }));
    }
}
