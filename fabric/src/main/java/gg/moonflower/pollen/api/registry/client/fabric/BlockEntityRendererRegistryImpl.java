package gg.moonflower.pollen.api.registry.client.fabric;

import gg.moonflower.pollen.api.registry.client.BlockEntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public class BlockEntityRendererRegistryImpl {
    public static <T extends BlockEntity> void register(Supplier<BlockEntityType<T>> type, BlockEntityRendererRegistry.BlockEntityRendererFactory<T> factory) {
        Minecraft minecraft = Minecraft.getInstance();
        net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry.INSTANCE.register(type.get(), renderDispatcher -> factory.create(new BlockEntityRendererRegistry.BlockEntityRendererFactory.Context() {
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
