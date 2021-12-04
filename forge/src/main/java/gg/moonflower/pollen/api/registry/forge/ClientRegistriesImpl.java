package gg.moonflower.pollen.api.registry.forge;

import gg.moonflower.pollen.api.registry.ClientRegistries;
import gg.moonflower.pollen.api.registry.ResourceRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlclient.registry.ClientRegistry;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistriesImpl {

    private static final Set<Consumer<EntityRenderersEvent.RegisterRenderers>> ENTITY_FACTORIES = new HashSet<>();

    @SubscribeEvent
    public static void onEvent(EntityRenderersEvent.RegisterRenderers event) {
        ENTITY_FACTORIES.forEach(consumer -> consumer.accept(event));
    }

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

    public static synchronized <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> factory) {
        ENTITY_FACTORIES.add(event -> event.registerEntityRenderer(type, factory));
    }

    public static <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type, BlockEntityRendererProvider<T> factory) {
        ENTITY_FACTORIES.add(event -> event.registerBlockEntityRenderer(type, factory));
    }

    public static void registerItemOverride(Item item, ResourceLocation id, ClampedItemPropertyFunction function) {
        ItemProperties.register(item, id, function);
    }

    public static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void registerScreenFactory(MenuType<M> type, ClientRegistries.ScreenFactory<M, S> factory) {
        MenuScreens.register(type, factory::create);
    }

    public static void registerAtlasSprite(ResourceLocation atlas, ResourceRegistry name) {
    }
}
