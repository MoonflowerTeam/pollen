package gg.moonflower.pollen.api.registry.fabric;

import gg.moonflower.pollen.api.registry.ClientRegistries;
import gg.moonflower.pollen.api.registry.ResourceRegistry;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ClientRegistriesImpl {

    public static void setBlockRenderType(Block block, RenderType type) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, type);
    }

    public static void setFluidRenderType(Fluid fluid, RenderType type) {
        BlockRenderLayerMap.INSTANCE.putFluid(fluid, type);
    }

    public static KeyMapping registerKeyBinding(KeyMapping key) {
        return KeyBindingHelper.registerKeyBinding(key);
    }

    public static <T extends Entity> void registerEntityRenderer(EntityType<T> type, ClientRegistries.EntityRendererFactory<T> factory) {
        EntityRendererRegistry.INSTANCE.register(type, (renderDispatcher, context) -> factory.create(new ClientRegistries.EntityRendererFactory.Context() {

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

    public static <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type, ClientRegistries.BlockEntityRendererFactory<T> factory) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockEntityRendererRegistry.INSTANCE.register(type, renderDispatcher -> factory.create(new ClientRegistries.BlockEntityRendererFactory.Context() {
            @Override
            public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
                return renderDispatcher;
            }

            @Override
            public BlockRenderDispatcher getBlockRenderDispatcher() {
                return minecraft.getBlockRenderer();
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

    public static void registerItemOverride(Item item, ResourceLocation id, ItemPropertyFunction function) {
        FabricModelPredicateProviderRegistry.register(item, id, function);
    }

    public static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void registerScreenFactory(MenuType<M> type, ClientRegistries.ScreenFactory<M, S> factory) {
        ScreenRegistry.register(type, factory::create);
    }
}
