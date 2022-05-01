package gg.moonflower.pollen.api.registry.client.forge;

import gg.moonflower.pollen.api.registry.client.EntityRendererRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRendererRegistryImpl {
    public static <T extends Entity> void register(EntityType<T> type, EntityRendererRegistry.EntityRendererFactory<T> factory) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderingRegistry.registerEntityRenderingHandler(type, renderDispatcher -> factory.create(new EntityRendererRegistry.EntityRendererFactory.Context() {
            @Override
            public EntityRenderDispatcher getEntityRenderDispatcher() {
                return renderDispatcher;
            }

            @Override
            public ItemRenderer getItemRenderer() {
                return minecraft.getItemRenderer();
            }

            @Override
            public ResourceManager getResourceManager() {
                return minecraft.getResourceManager();
            }

            @Override
            public Font getFont() {
                return minecraft.font;
            }
        }));
    }
}
