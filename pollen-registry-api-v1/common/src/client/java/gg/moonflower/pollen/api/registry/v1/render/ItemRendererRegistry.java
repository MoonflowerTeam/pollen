package gg.moonflower.pollen.api.registry.v1.render;

import gg.moonflower.pollen.api.registry.v1.render.DynamicItemRenderer;
import gg.moonflower.pollen.impl.registry.ItemRendererRegistryImpl;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

/**
 * Registers custom renderers for items.
 *
 * @see DynamicItemRenderer
 * @since 1.0.0
 */
public interface ItemRendererRegistry {

    /**
     * Registers a dynamic renderer for the specified item.
     *
     * @param item     The item to register the renderer for
     * @param renderer The renderer to use
     */
    static void registerRenderer(ItemLike item, DynamicItemRenderer renderer) {
        ItemRendererRegistryImpl.registerRenderer(item, renderer);
    }

    /**
     * Registers the standard definition of a "hand" model. Includes all except gui, ground, and fixed. The model must be registered separately.
     *
     * @param item      The item to register a model for
     * @param handModel The hand model
     */
    static void registerHandModel(ItemLike item, ModelResourceLocation handModel) {
        ItemRendererRegistryImpl.registerHandModel(item, handModel);
    }

    /**
     * Retrieves a model for a transform.
     *
     * @param item The item to get the model for
     * @return The model for that item or <code>null</code> for default
     */
    @Nullable
    static ModelResourceLocation getHandModel(Item item) {
        return ItemRendererRegistryImpl.getHandModel(item);
    }
}
