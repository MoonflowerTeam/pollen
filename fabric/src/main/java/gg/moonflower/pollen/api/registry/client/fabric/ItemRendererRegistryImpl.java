package gg.moonflower.pollen.api.registry.client.fabric;

import gg.moonflower.pollen.api.client.render.DynamicItemRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ItemRendererRegistryImpl {

    public static void registerRenderer(ItemLike item, DynamicItemRenderer renderer) {
        BuiltinItemRendererRegistry.INSTANCE.register(item, renderer::render);
    }
}
