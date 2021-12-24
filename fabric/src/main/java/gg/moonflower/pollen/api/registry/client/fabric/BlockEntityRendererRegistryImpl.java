package gg.moonflower.pollen.api.registry.client.fabric;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BlockEntityRendererRegistryImpl {
    public static <T extends BlockEntity> void register(BlockEntityType<T> type, BlockEntityRendererProvider<T> factory) {
        net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.register(type, factory);
    }

}
