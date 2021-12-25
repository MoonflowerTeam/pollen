package gg.moonflower.pollen.api.registry.client.forge;

import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockEntityRendererRegistryImpl {

    private static final Set<Consumer<EntityRenderersEvent.RegisterRenderers>> BLOCK_ENTITY_FACTORIES = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public static void onEvent(EntityRenderersEvent.RegisterRenderers event) {
        BLOCK_ENTITY_FACTORIES.forEach(consumer -> consumer.accept(event));
    }

    public static <T extends BlockEntity> void register(Supplier<BlockEntityType<T>> type, BlockEntityRendererProvider<T> factory) {
        BLOCK_ENTITY_FACTORIES.add(event -> event.registerBlockEntityRenderer(type.get(), factory));
    }
}
