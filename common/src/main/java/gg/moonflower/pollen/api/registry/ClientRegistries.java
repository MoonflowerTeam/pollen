package gg.moonflower.pollen.api.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Function;

/**
 * @author Jackson
 * @since 1.0.0
 */
@Environment(EnvType.CLIENT)
public final class ClientRegistries {

    private ClientRegistries() {
    }

    @ExpectPlatform
    public static void setBlockRenderType(Block block, RenderType type) {
        Platform.error();
    }

    @ExpectPlatform
    public static void setFluidRenderType(Fluid fluid, RenderType type) {
        Platform.error();
    }

    @ExpectPlatform
    public static KeyMapping registerKeyBinding(KeyMapping key) {
        return Platform.error();
    }

    @ExpectPlatform
    public static <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererFactory<T> factory) {
        Platform.error();
    }

    @ExpectPlatform
    public static <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type, Function<BlockEntityRenderDispatcher, BlockEntityRenderer<? super T>> factory) {
        Platform.error();
    }

    @ExpectPlatform
    public static void registerItemOverride(Item item, ResourceLocation id, ItemPropertyFunction function) {
        Platform.error();
    }

    @ExpectPlatform
    public static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void registerScreenFactory(MenuType<M> type, ScreenFactory<M, S> object) {
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

    @FunctionalInterface
    public interface ScreenFactory<M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> {
        S create(M menu, Inventory inventory, Component title);
    }
}
