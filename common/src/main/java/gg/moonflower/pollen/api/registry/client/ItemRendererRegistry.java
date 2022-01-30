package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.client.render.DynamicItemRenderer;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registers custom renderers for items.
 *
 * @see DynamicItemRenderer
 * @since 1.0.0
 */
public final class ItemRendererRegistry {

    private static final Map<Item, ModelResourceLocation> MODELS = new ConcurrentHashMap<>();

    private ItemRendererRegistry() {
    }

    @ExpectPlatform
    public static void registerRenderer(ItemLike item, DynamicItemRenderer renderer) {
        Platform.error();
    }

    /**
     * Registers the standard definition of a "hand" model. Includes all except gui, ground, and fixed. The model must be registered separately.
     *
     * @param item      The item to register a model for
     * @param handModel The hand model
     */
    public static void registerHandModel(ItemLike item, ModelResourceLocation handModel) {
        MODELS.put(item.asItem(), handModel);
    }

    /**
     * Retrieves a model for a transform.
     *
     * @param item The item to get the model for
     * @return The model for that item or <code>null</code> for default
     */
    @Nullable
    public static ModelResourceLocation getHandModel(Item item) {
        return MODELS.get(item);
    }
}
