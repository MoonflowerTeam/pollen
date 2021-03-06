package gg.moonflower.pollen.api.registry.client.forge;

import gg.moonflower.pollen.api.registry.client.BlockEntityRendererRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockEntityRendererRegistryImpl {
    public static <T extends BlockEntity> void register(Supplier<BlockEntityType<T>> type, BlockEntityRendererRegistry.BlockEntityRendererFactory<T> factory) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientRegistry.bindTileEntityRenderer(type.get(), renderDispatcher -> factory.create(new BlockEntityRendererRegistry.BlockEntityRendererFactory.Context() {
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

}
