package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public final class BlockEntityRendererRegistry {

    private BlockEntityRendererRegistry() {
    }

    @ExpectPlatform
    public static <T extends BlockEntity> void register(Supplier<BlockEntityType<T>> type, BlockEntityRendererFactory<T> factory) {
        Platform.error();
    }

    @FunctionalInterface
    public interface BlockEntityRendererFactory<T extends BlockEntity> {
        BlockEntityRenderer<T> create(Context context);

        interface Context {

            BlockEntityRenderDispatcher getBlockEntityRenderDispatcher();

            BlockRenderDispatcher getBlockRenderDispatcher();

            ResourceManager getResourceManager();

            Font getFont();
        }
    }
}
