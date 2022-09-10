package gg.moonflower.pollen.api.registry.v1.render;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.base.platform.Platform;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public interface BlockEntityRendererRegistry {

    @ExpectPlatform
    static <T extends BlockEntity> void register(Supplier<BlockEntityType<T>> type, BlockEntityRendererProvider<T> factory) {
        Platform.error();
    }
}
