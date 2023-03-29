package gg.moonflower.pollen.impl.registry.render.fabric;

import gg.moonflower.pollen.api.render.item.v1.DynamicItemRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.world.level.ItemLike;

public class ItemRendererRegistryImplImpl {
    public static void registerRenderer(ItemLike item, DynamicItemRenderer renderer) {
        BuiltinItemRendererRegistry.INSTANCE.register(item, renderer::render);
    }
}
