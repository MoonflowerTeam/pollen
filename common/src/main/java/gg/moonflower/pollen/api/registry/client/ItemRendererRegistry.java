package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.client.render.DynamicItemRenderer;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Registers custom renderers for items.
 *
 * @see DynamicItemRenderer
 * @since 1.0.0
 */
public final class ItemRendererRegistry {

    private static final Map<Item, Map<ItemTransforms.TransformType, ModelResourceLocation>> MODELS = new HashMap<>();
    private static final ReentrantLock MODELS_LOCK = new ReentrantLock();

    private ItemRendererRegistry() {
    }

    @ExpectPlatform
    public static void registerRenderer(ItemLike item, DynamicItemRenderer renderer) {
        Platform.error();
    }

    /**
     * Registers the standard definition of a "gui" model. Includes gui, ground, and fixed. The model must be registered separately.
     *
     * @param item      The item to register a model for
     * @param modelName The gui model
     */
    public static void registerGuiModel(ItemLike item, ModelResourceLocation modelName) {
        registerPerspectiveModel(item, modelName, ItemTransforms.TransformType.GUI, ItemTransforms.TransformType.GROUND, ItemTransforms.TransformType.FIXED);
    }

    /**
     * Registers a model for each perspective of an item. The model must be registered separately.
     *
     * @param item       The item to register a model for
     * @param modelName  The name of the model to register
     * @param transforms The transforms to register the model for
     */
    public static void registerPerspectiveModel(ItemLike item, ModelResourceLocation modelName, ItemTransforms.TransformType... transforms) {
        MODELS_LOCK.lock();
        Map<ItemTransforms.TransformType, ModelResourceLocation> map = MODELS.computeIfAbsent(item.asItem(), __ -> new HashMap<>());
        Arrays.stream(transforms).distinct().forEach(transform -> map.put(transform, modelName));
        MODELS_LOCK.unlock();
    }

    /**
     * Retrieves a model for a transform.
     *
     * @param item      The item to get the model for
     * @param transform The transform to get the model for
     * @return The model for that item or <code>null</code> for default
     */
    @Nullable
    public static ModelResourceLocation getModel(Item item, ItemTransforms.TransformType transform) {
        MODELS_LOCK.lock();
        try {
            if (!MODELS.containsKey(item))
                return null;
            return MODELS.get(item).get(transform);
        } finally {
            MODELS_LOCK.unlock();
        }
    }
}
