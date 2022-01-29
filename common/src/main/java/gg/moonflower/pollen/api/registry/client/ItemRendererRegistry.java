package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.client.render.DynamicItemRenderer;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.world.level.ItemLike;

/**
 * Registers custom renderers for items.
 *
 * @see DynamicItemRenderer
 * @since 1.0.0
 */
public final class ItemRendererRegistry {

    private ItemRendererRegistry() {
    }

    @ExpectPlatform
    public static void registerRenderer(ItemLike item, DynamicItemRenderer renderer) {
        Platform.error();
    }
}
