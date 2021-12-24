package gg.moonflower.pollen.api.registry.client.forge;

import gg.moonflower.pollen.api.registry.client.BlockEntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BlockEntityRendererRegistryImpl {
    public static <T extends BlockEntity> void register(BlockEntityType<T> type, BlockEntityRendererRegistry.BlockEntityRendererFactory<T> factory) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientRegistry.bindTileEntityRenderer(type, renderDispatcher -> factory.create(new BlockEntityRendererRegistry.BlockEntityRendererFactory.Context() {
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
