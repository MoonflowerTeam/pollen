package gg.moonflower.pollen.api.registry.client.forge;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@ApiStatus.Internal
public class BlockEntityRendererRegistryImpl {

    private static final Set<Consumer<EntityRenderersEvent.RegisterRenderers>> BLOCK_ENTITY_FACTORIES = new HashSet<>();

    @SubscribeEvent
    public static void onEvent(EntityRenderersEvent.RegisterRenderers event) {
        BLOCK_ENTITY_FACTORIES.forEach(consumer -> consumer.accept(event));
    }

    public static <T extends BlockEntity> void register(BlockEntityType<T> type, BlockEntityRendererProvider<T> factory) {
        BLOCK_ENTITY_FACTORIES.add(event -> event.registerBlockEntityRenderer(type, factory));
    }
}
