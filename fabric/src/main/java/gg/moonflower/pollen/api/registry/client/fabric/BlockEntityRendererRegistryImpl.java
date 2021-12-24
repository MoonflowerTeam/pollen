package gg.moonflower.pollen.api.registry.client.fabric;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public class BlockEntityRendererRegistryImpl {

    public static <T extends BlockEntity> void register(Supplier<BlockEntityType<T>> type, BlockEntityRendererProvider<T> factory) {
        net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.register(type.get(), factory);
    }

}
