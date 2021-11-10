package gg.moonflower.pollen.api.registry.forge;

import gg.moonflower.pollen.api.registry.ClientRegistries;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.function.Function;

public class ClientRegistriesImpl {

    public static void setBlockRenderType(Block block, RenderType type) {
        ItemBlockRenderTypes.setRenderLayer(block, type);
    }

    public static void setFluidRenderType(Fluid fluid, RenderType type) {
        ItemBlockRenderTypes.setRenderLayer(fluid, type);
    }

    public static KeyMapping registerKeyBinding(KeyMapping key) {
        ClientRegistry.registerKeyBinding(key);
        return key;
    }

    public static <T extends Entity> void registerEntityRenderer(EntityType<T> type, ClientRegistries.EntityRendererFactory<T> factory) {
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        dispatcher.register(type, factory.create(dispatcher, new ClientRegistries.EntityRendererRegistryContext() {
            @Override
            public TextureManager getTextureManager() {
                return Minecraft.getInstance().getTextureManager();
            }

            @Override
            public ReloadableResourceManager getResourceManager() {
                return (ReloadableResourceManager) Minecraft.getInstance().getResourceManager();
            }

            @Override
            public ItemRenderer getItemRenderer() {
                return Minecraft.getInstance().getItemRenderer();
            }
        }));
    }

    public static <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type, Function<BlockEntityRenderDispatcher, BlockEntityRenderer<? super T>> factory) {
        ClientRegistry.bindTileEntityRenderer(type, factory);
    }

    public static void registerItemOverride(Item item, ResourceLocation id, ItemPropertyFunction function) {
        ItemProperties.register(item, id, function);
    }

    public static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void registerScreenFactory(MenuType<M> type, ClientRegistries.ScreenFactory<M, S> factory) {
        MenuScreens.register(type, factory::create);
    }
}
