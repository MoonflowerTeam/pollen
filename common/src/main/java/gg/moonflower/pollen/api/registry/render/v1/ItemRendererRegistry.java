package gg.moonflower.pollen.api.registry.render.v1;

import gg.moonflower.pollen.api.render.item.v1.DynamicItemRenderer;
import gg.moonflower.pollen.impl.registry.render.ItemRendererRegistryImpl;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

/**
 * Registers custom renderers for items.
 *
 * @see DynamicItemRenderer
 * @since 2.0.0
 */
public interface ItemRendererRegistry {

    /**
     * Registers a new custom item renderer.
     *
     * @param item     The item to register for the renderer
     * @param renderer The renderer to use instead of the normal baked model rendering
     * @see DynamicItemRenderer
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
